package com.michalkowol.cars;

import com.softwareberg.JsonMapper;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static com.michalkowol.Errors.BadRequestException;
import static com.michalkowol.Errors.NotFoundException;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static spark.Spark.*;

@Singleton
public class CarsController {

    private final CarsRepository carsRepository;
    private final JsonMapper jsonMapper;

    @Inject
    CarsController(CarsRepository carsRepository, JsonMapper jsonMapper) {
        this.carsRepository = carsRepository;
        this.jsonMapper = jsonMapper;
    }

    public void start() {
        get("/cars", this::cars, jsonMapper::write);
        post("/cars", this::createCar, jsonMapper::write);
        get("/cars/create", this::createCarWithQueryParams, jsonMapper::write);
        get("/cars/:id", this::carById, jsonMapper::write);
        put("/cars/:id", this::changeCarName, jsonMapper::write);
        delete("/cars/:id", this::deleteCar, jsonMapper::write);
    }

    private List<Car> cars(Request request, Response response) {
        response.type("application/json");
        List<Car> cars = carsRepository.findAll();
        return cars;
    }

    private Car carById(Request request, Response response) {
        response.type("application/json");
        int id = Integer.parseInt(request.params("id"));
        return carsRepository.byId(id).orElseThrow(() -> new NotFoundException("Car with id=" + id + " not found"));
    }

    private Car createCar(Request request, Response response) {
        response.type("application/json");
        response.status(HTTP_CREATED);
        Car car = jsonMapper.read(request.body(), Car.class);
        return carsRepository.create(car.getId(), car.getName());
    }

    private Car createCarWithQueryParams(Request request, Response response) {
        // not RESTfull - only for demo
        response.type("application/json");
        response.status(HTTP_CREATED);
        int id = Optional.ofNullable(request.queryParams("id")).map(Integer::parseInt).orElseThrow(() -> new BadRequestException("Missing 'id' query param"));
        String name = Optional.ofNullable(request.queryParams("name")).orElseThrow(() -> new BadRequestException("Missing 'name' query param"));
        return carsRepository.create(id, name);
    }

    private Car changeCarName(Request request, Response response) {
        response.type("application/json");
        int idFromParam = Integer.parseInt(request.params("id"));
        Car car = jsonMapper.read(request.body(), Car.class);
        if (car.getId() != idFromParam) {
            throw new BadRequestException("Request id is not equal Car.id");
        }
        carsRepository.changeName(car.getId(), car.getName());
        return carsRepository.byId(car.getId()).orElseThrow(() -> new NotFoundException("Car with id=" + car.getId() + " not found"));
    }

    private String deleteCar(Request request, Response response) {
        response.type("application/json");
        response.status(HTTP_NO_CONTENT);
        int id = Integer.parseInt(request.params("id"));
        carsRepository.delete(id);
        return "";
    }
}
