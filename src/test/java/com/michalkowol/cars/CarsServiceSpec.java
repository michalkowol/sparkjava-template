package com.michalkowol.cars;

import com.google.common.collect.ImmutableList;
import com.softwareberg.Database;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CarsServiceSpec {

    @Test
    public void itShouldFindAllCars() {
        // given
        Database db = mock(Database.class);
        when(db.findAll(any(String.class), any())).thenReturn(ImmutableList.of(new Car(1, "Audi"), new Car(2, "Ford")));
        CarsRepository carsRepository = new CarsRepository(db);
        // when
        List<Car> cars = carsRepository.findAll();
        // then
        assertThat(cars, hasSize(2));
        assertThat(cars.get(0), equalTo(new Car(1, "Audi")));
    }
}
