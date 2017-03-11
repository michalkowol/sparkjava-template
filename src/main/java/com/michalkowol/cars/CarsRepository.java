package com.michalkowol.cars;

import com.google.common.collect.ImmutableMap;
import com.softwareberg.Database;
import com.softwareberg.NamedSqlStatement;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
class CarsRepository {

    private final Database database;

    @Inject
    CarsRepository(Database database) {
        this.database = database;
    }

    List<Car> findAll() {
        return database.findAll("SELECT id, name FROM cars", row -> new Car(row.intValue("id"), row.string("name")));
    }

    Car create(int id, String name) {
        NamedSqlStatement sql = new NamedSqlStatement("INSERT INTO cars (id, name) VALUES (:id, :name)", ImmutableMap.of("id", id, "name", name));
        database.insert(sql);
        return new Car(id, name);
    }

    int changeName(int id, String name) {
        NamedSqlStatement sql = new NamedSqlStatement("UPDATE cars SET name = :name WHERE id = :id", ImmutableMap.of("id", id, "name", name));
        int updatedCount = database.update(sql);
        return updatedCount;
    }

    Optional<Car> byId(int id) {
        NamedSqlStatement sql = new NamedSqlStatement("SELECT id, name FROM cars WHERE id = :id", ImmutableMap.of("id", id));
        return Optional.ofNullable(database.findOne(sql, row -> new Car(row.intValue("id"), row.string("name"))));
    }

    int delete(int id) {
        NamedSqlStatement sql = new NamedSqlStatement("DELETE FROM cars WHERE id = :id", ImmutableMap.of("id", id));
        int deletedCount = database.update(sql);
        return deletedCount;
    }
}
