package net.chrisrichardson.ftgo.endtoendtests;

import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;

public interface ApplicationUnderTest {

    static ApplicationUnderTest make() {
        try {
            String mode = System.getProperty("endToEndTestMode", "DockerCompose");
            String className = ApplicationUnderTest.class.getName() + "Using" + mode;
            Class<?> clazz = ClassUtils.forName(className, ApplicationUnderTest.class.getClassLoader());
            return (ApplicationUnderTest) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    void start();

    void stop();

    String getHost();

    int getConsumerServicePort();

    int getOrderServicePort();

    int getKitchenServicePort();

    int getRestaurantServicePort();

    int getAccountingServicePort();

    int getOrderHistoryServicePort();

    int getApiGatewayPort();

    int getDeliveryServicePort();
}
