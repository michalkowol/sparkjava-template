package com.michalkowol.cars;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.ninja_squad.dbsetup.operation.Operation;
import com.softwareberg.Database;
import com.softwareberg.JsonMapper;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import spark.servlet.SparkApplication;

import static com.michalkowol.DatabaseResource.DataSourceResource;
import static com.michalkowol.DatabaseResource.H2DatabaseResource;
import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class CarsControllerIntegrationSpec {

    public static class TestSparkApplication implements SparkApplication {
        @Override
        public void init() {
            new CarsController(new CarsRepository(new Database(dataSourceResource.getDataSource())), JsonMapper.Companion.create()).start();
        }
    }

    private static H2DatabaseResource h2DatabaseResource = new H2DatabaseResource();

    private static DataSourceResource dataSourceResource = new DataSourceResource();

    private static SparkServer<TestSparkApplication> testServer = new SparkServer<>(CarsControllerIntegrationSpec.TestSparkApplication.class, 4567);

    @ClassRule
    public static RuleChain rules = RuleChain
        .outerRule(h2DatabaseResource)
        .around(dataSourceResource)
        .around(testServer);

    @BeforeClass
    public static void setup() {
        dataSourceResource.cleanAndMigrateDatabase();
    }

    private Operation deleteAllCars = deleteAllFrom("cars");

    private Operation insertCars = insertInto("cars")
        .columns("id", "name")
        .values(1, "Audi")
        .values(2, "VW")
        .build();

    @Test
    public void itShouldFindAllCarsWithRest() throws HttpClientException {
        // given
        dataSourceResource.prepareDatabase(sequenceOf(deleteAllCars, insertCars));
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
