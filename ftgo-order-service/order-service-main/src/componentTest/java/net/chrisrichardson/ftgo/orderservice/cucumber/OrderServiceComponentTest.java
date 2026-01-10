package net.chrisrichardson.ftgo.orderservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/componentTest/resources/features")
public class OrderServiceComponentTest {
}
