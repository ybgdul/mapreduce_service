package mapreduce.app.entities;

import org.springframework.stereotype.Service;

import mapreduce.app.utilities.DTOs.JobContext;
import smile.regression.LinearModel;

@Service
public class RuntimeEstimationModel {
    
    private final LinearModel linearModel;

    public RuntimeEstimationModel() { 
        this.linearModel = null;
    }

    public double predict(JobContext features) { 
        double[] values = {
            features.remainingTasks(),
            features.inputSize(),
            features.threadPoolSize(),
            features.runningTasks(),
            features.AverageWorkingTime()
        };

        return linearModel.predict(values);
    }
}
