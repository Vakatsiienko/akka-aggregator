package com.akka.testProject;

/**
 * Created by Iaroslav on 11/12/2015.
 */
public class CalculationServiceFactory {

    public CalculationService getCalcServiceInstance(int actorPoolSize){
        return new CalculationService(actorPoolSize);
    }
}
