package org.wit.myrent.retrofit;

import org.wit.myrent.models.Residence;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ResidenceServiceProxy
{
  @POST("/api/residence")
  Call<Residence> createResidence(@Body Residence residence);

  @DELETE("/api/residences/{id}")
  Call<String> deleteResidence(@Path("id") Long id);

  @POST("/api/residence/update")
  Call<Residence> updateResidence(@Body Residence residence);

  @GET("/api/residences")
  Call<List<Residence>> getResidences();

}
