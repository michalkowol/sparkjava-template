package com.michalkowol.cars;

import com.ninja_squad.dbsetup.operation.Operation;
import com.softwareberg.Database;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.michalkowol.DatabaseResource.DataSourceResource;
import static com.michalkowol.DatabaseResource.H2DatabaseResource;
import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class CarsServiceIntegrationSpec {

    private static H2DatabaseResource h2DatabaseResource = new H2DatabaseResource();

    private static DataSourceResource dataSourceResource = new DataSourceResource();

    @ClassRule
    public static RuleChain rules = RuleChain
        .outerRule(h2DatabaseResource)
        .around(dataSourceResource);

    @BeforeClass
    public static void setup() {
        dataSourceResource.cleanAndMigrateDatabase();
    }

    private Operation deleteAllCars = deleteAllFrom("cars");

    private Operation insertCars = insertInto("cars")
        .columns("id", "name")
        .values(1, "Audi")
        .values(2, "Opel")
        .values(3, "BMW")
        .build();

    @Test
    public void itShouldFindAllCars() {
        // given
        dataSourceResource.prepareDatabase(sequenceOf(deleteAllCars, insertCars));
        CarsRepository carsRepository = new CarsRepository(new Database(dataSourceResource.getDataSource()));
        // when
        List<Car> cars = carsRepository.findAll();
        // then
        assertThat(cars, hasSize(3));
        assertThat(cars.get(0), equalTo(new Car(1, "Audi")));
        assertThat(cars.get(1), equalTo(new Car(2, "Opel")));
        assertThat(cars.get(2), equalTo(new Car(3, "BMW")));
    }
}
