/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.recommendation;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.tensorflow.lite.examples.recommendation.RecommendationClient.Result;
import org.tensorflow.lite.examples.recommendation.data.FileUtil;
import org.tensorflow.lite.examples.recommendation.data.MovieItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** The main activity to provide interactions with users. */
public class MainActivity extends AppCompatActivity
    implements MovieFragment.OnListFragmentInteractionListener,
        RecommendationFragment.OnListFragmentInteractionListener {
  private static final String TAG = "OnDeviceRecommendationDemo";
  private static final String CONFIG_PATH = "config.json";
  public Integer PAGE = 1;
  public static String API_KEY ="39c5465cd4d393531f1e739433a8e360";
  public static String CATEGORY ="popular";
  public static String LANGUAGE ="tr-tr";
    public static String BASE_URL=  "https://api.themoviedb.org";
  public static Map<Integer,String> aaaa = new HashMap<Integer,String>();

  private Config config;
  private RecommendationClient client;
  private final List<MovieItem> allMovies = new ArrayList<>();
  private final List<MovieItem> selectedMovies = new ArrayList<>();
  public static List<MovieResults.Result> tmdbResults = new ArrayList<>();
  public static ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();

  private Handler handler;
  private MovieFragment movieFragment;
  private RecommendationFragment recommendationFragment;
  Dialog dialog;
  ImageView dialogImage;
  TextView dialogMovieName;
  TextView dialogMovieDesc;
  private ApiInterface myInterface;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.v(TAG, "onCreate");

    dialog = new Dialog(this);
    dialog.setContentView(R.layout.movie_clicked);
    dialogImage = dialog.findViewById(R.id.movieImage);
    dialogMovieName = dialog.findViewById(R.id.movieName);
    dialogMovieDesc = dialog.findViewById(R.id.movieDescription);

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(
              new InputStreamReader(this.getAssets().open("links.csv"), "UTF-8"));

      // do reading, usually loop until end of file reading
      String mLine;
      while ((mLine = reader.readLine()) != null) {
        //process line
        ArrayList<Integer> aa = new ArrayList<>();
        String [] a =mLine.split(",");
        for(String x: a){
          aa.add(Integer.parseInt(x));
        }

        arrayList.add(aa);
      }
    } catch (IOException e) {
      //log the exception
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          //log the exception
        }
      }
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    myInterface = retrofit.create(ApiInterface.class);

    /*for (int i=0; i<500;i++) {
      getPageOfMovies(myInterface);
      PAGE++;
    }*/

    // Load config file.
    try {
      config = FileUtil.loadConfig(getAssets(), CONFIG_PATH);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Error occurs when loading config %s: %s.", CONFIG_PATH, ex));
    }

    // Load movies list.
    try {
      allMovies.clear();
      allMovies.addAll(FileUtil.loadMovieList(getAssets(), config.movieList));
    } catch (IOException ex) {
      Log.e(TAG, String.format("Error occurs when loading movies %s: %s.", config.movieList, ex));
    }

    client = new RecommendationClient(this, config);
    handler = new Handler();
    movieFragment =
        (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
    recommendationFragment =
        (RecommendationFragment)
            getSupportFragmentManager().findFragmentById(R.id.recommendation_fragment);
  }

  @SuppressWarnings("AndroidJdkLibsChecker")
  @Override
  protected void onStart() {
    super.onStart();
    Log.v(TAG, "onStart");

    // Add favorite movies to the fragment.
    List<MovieItem> favoriteMovies =
        allMovies.stream().limit(config.favoriteListSize).collect(Collectors.toList());

    movieFragment.setMovies(favoriteMovies);

    handler.post(
        () -> {
          client.load();
        });
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.v(TAG, "onStop");
    handler.post(
        () -> {
          client.unload();
        });
  }

  /** Sends selected movie list and get recommendations. */
  private void recommend(final List<MovieItem> movies) {
    handler.post(
        () -> {
          // Run inference with TF Lite.
          Log.d(TAG, "Run inference with TFLite model.");
          List<Result> recommendations = client.recommend(movies);

          // Show result on screen
          showResult(recommendations);
        });
  }

  private void showResult(final List<Result> recommendations) {
    // Run on UI thread as we'll updating our app UI
    runOnUiThread(() -> recommendationFragment.setRecommendations(recommendations));
  }

  @Override
  public void onItemSelectionChange(MovieItem item) {
    if (item.selected) {
      if (!selectedMovies.contains(item)) {
        selectedMovies.add(item);
      }
    } else {
      selectedMovies.remove(item);
    }

    if (selectedMovies.size()>4) {
      // Log selected movies.
      StringBuilder sb = new StringBuilder();
      sb.append("Select movies in the following order:\n");
      for (MovieItem movie : selectedMovies) {
        sb.append(String.format("  movie: %s\n", movie));
      }
      Log.d(TAG, sb.toString());

      // Recommend based on selected movies.
      recommend(selectedMovies);
    } else {
      // Clear result list.
      showResult(new ArrayList<Result>());
    }
  }

  /** Handles click event of recommended movie. */
  @Override
  public void onClickRecommendedMovie(MovieItem item) {

    Integer id = 0;
    String message = String.format("Clicked recommended movie: %s.", item.title);
    for (int i = 0; i < arrayList.size(); i++) {
      if (arrayList.get(i).get(0) == item.id) {
        Integer a = arrayList.get(i).get(2);
        id = a;
        String imageLink = "https://enessolak.com.tr/test2.php?id="+a.toString();
        Picasso.get().load(imageLink).into(dialogImage);
        dialogMovieName.setText(item.title);
      }
    }

    Call<MovieData> call = myInterface.getMovie(id,API_KEY);
    call.enqueue(new Callback<MovieData>() {
      @Override
      public void onResponse(Call<MovieData> call, Response<MovieData> response) {
        MovieData result = response.body();
        //List<MovieResults.Result> listOfMovies = results.getResults();
        dialogMovieDesc.setText(result.getOverview());
      }
      @Override
      public void onFailure(Call<MovieData> call, Throwable t) {
      }
    });


    dialog.show();
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  public void getPageOfMovies(ApiInterface myInterface){

    Call<MovieResults> call = myInterface.listOfMovies(CATEGORY, API_KEY, LANGUAGE, PAGE);
    call.enqueue(new Callback<MovieResults>() {
      @Override
      public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
        MovieResults results = response.body();
        List<MovieResults.Result> listOfMovies = results.getResults();

        for(int i=0;i<listOfMovies.size();i++){
          aaaa.put(listOfMovies.get(i).getId(),"https://image.tmdb.org/t/p/w500/"+listOfMovies.get(i).getPosterPath());
          tmdbResults.add(listOfMovies.get(i));
        }
      }
      @Override
      public void onFailure(Call<MovieResults> call, Throwable t) {
      }
    });

  }
}
