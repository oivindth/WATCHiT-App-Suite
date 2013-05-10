package asynctasks;

import java.io.IOException;
import no.ntnu.emergencyreflect.CloudEndpointUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.no.ntnu.emergencyreflect.procedureendpoint.Procedureendpoint;

import com.google.api.services.no.ntnu.emergencyreflect.procedureendpoint.model.Procedure;

import com.google.api.services.no.ntnu.emergencyreflect.resultendpoint.model.Result;
import com.google.api.services.no.ntnu.emergencyreflect.stependpoint.Stependpoint;
import com.google.api.services.no.ntnu.emergencyreflect.stependpoint.model.Step;
import com.google.api.services.no.ntnu.emergencyreflect.userendpoint.model.User;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Asynctask used for communicating with Google App Engine backend.
 * @author oivindth
 *
 */
public class EndPointsTestTask extends AsyncTask<Context, Integer, Long>  {

	protected Long doInBackground(Context... contexts) {

		
		Procedureendpoint.Builder procedureEndpointBuilder = new Procedureendpoint.Builder(AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),  new HttpRequestInitializer() {
			public void initialize(HttpRequest httpRequest) { }
		});
		
		Stependpoint.Builder stepEndpointBuilder = new Stependpoint.Builder(AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),  new HttpRequestInitializer() {
			public void initialize(HttpRequest httpRequest) { }
		});
		
		Stependpoint endpoint = CloudEndpointUtils.updateBuilder(stepEndpointBuilder).build();
		
		Procedureendpoint pendPoint = CloudEndpointUtils.updateBuilder(procedureEndpointBuilder).build();
		
    try {
        Procedure procedure = new Procedure();
      	procedure.setName("Test");
      	procedure.setDescription("train for test");
      	Procedure presult = pendPoint.insertProcedure(procedure).execute();
      	
      	
      	
        Step step = new Step().setName("Step 1");
        Step result = endpoint.insertStep(step).execute();
        step.setProcedure(procedure);
        step.setProcedure(procedure);
        
        
        
        Step step2 = new Step().setName("Step 2");
        step.setTime("");
        step2.setProcedure(procedure);
        Step result2 = endpoint.insertStep(step2).execute();
        
        
        User user = new User();
        user.setName("James");
        user.setJid("1223");
        
        
        Result result11 = new Result();
        result11.setTime("02:20");
        result11.setStep(step);
        result11.sets
        
  
    } catch (IOException e) {
      e.printStackTrace();
    }
        return (long) 0;
      }
  }


