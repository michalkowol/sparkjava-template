package com.michalkowol.cars;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.ninja_squad.dbsetup.operation.Operation;
import com.softwareberg.Database;
import com.softwareberg.JsonMapper;
import com.michalkowol.DatabaseIntegrationSpec;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class CarsRouteIntegrationSpec extends DatabaseIntegrationSpec {

    public static class TestSparkApplication implements SparkApplication {
        @Override
        public void init() {
            new CarsController(new CarsRepository(new Database(dataSource)), JsonMapper.Companion.create()).start();
        }
    }

    @ClassRule
    public static SparkServer<TestSparkApplication> testServer = new SparkServer<>(CarsRouteIntegrationSpec.TestSparkApplication.class, 4567);

    private Operation deleteAllCars = deleteAllFrom("cars");

    private Operation insertCars = insertInto("cars")
        .columns("id", "name")
        .values(1, "Audi")
        .values(2, "VW")
        .build();

    @Test
    public void itShouldFindAllCarsWithRest() throws HttpClientException {
        // given
        prepareDatabase(sequenceOf(deleteAllCars, insertCars));
        GetMethod get = testServer.get("/cars", false);

        // when
        HttpResponse httpResponse = testServer.execute(get);
        String json = new String(httpResponse.body());

        // then
        assertThat(httpResponse.code(), equalTo(200));
        assertThat(json, startsWith("[{\"id\":1"));
        assertThat(json, containsString("VW"));
    }
}
