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

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import org.tensorflow.lite.examples.recommendation.MovieFragment.OnListFragmentInteractionListener;
import org.tensorflow.lite.examples.recommendation.data.MovieItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRecyclerViewAdapter
    extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
  private final List<MovieItem> values;
  private final OnListFragmentInteractionListener listener;
  public static String BASE_URL=  "https://api.themoviedb.org";
  public String posterBaseUrl = "https://image.tmdb.org/t/p/w500/";
  public Integer PAGE = 1;
  public static String API_KEY ="39c5465cd4d393531f1e739433a8e360";
  public static String CATEGORY ="popular";
  public static String LANGUAGE ="tr-tr";
  public static String TEXT ="";
  public static String IMAGE_LINK ="";


  public MovieRecyclerViewAdapter(
          List<MovieItem> items, OnListFragmentInteractionListener listener) {
    values = items;
    this.listener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.selection, parent, false);
    return new ViewHolder(view, listener);

  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.item = values.get(position);
    holder.movieSwitch.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Switch sw = ((Switch) v);
            // Use checked status.
            boolean selected = sw.isChecked();
            holder.setSelected(selected);
          }
        });
    holder.movieTitle.setText(values.get(position).title);


    IMAGE_LINK = "https://i.imgur.com/DvpvklR.png";

    for (int i = 0; i < MainActivity.arrayList.size(); i++) {
      if (MainActivity.arrayList.get(i).get(0) == values.get(position).id) {
       Integer a = MainActivity.arrayList.get(i).get(2);
       IMAGE_LINK = "https://enessolak.com.tr/test2.php?id="+a.toString();
      }
    }

   try {
      //IMAGE_LINK = posterBaseUrl+ values.get(position).poster;
      Picasso.get().load(IMAGE_LINK).into(holder.movieImage);

    }catch (Exception e){
      IMAGE_LINK = "https://i.imgur.com/DvpvklR.png";
      Picasso.get().load(IMAGE_LINK).into(holder.movieImage);
    }

    //IMAGE_LINK = MainActivity.aaaa.get((MainActivity.gfg.get(values.get(position).id)));
    Picasso.get().load(IMAGE_LINK).into(holder.movieImage);

    PAGE=1;



    holder.view.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // Toggle checked status.
            boolean selected = !holder.movieSwitch.isChecked();
            holder.setSelected(selected);
          }
        });
  }


  @Override
  public int getItemCount() {
    return values.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public final View view;
    public final Switch movieSwitch;
    public final TextView movieTitle;
    public final ImageView movieImage;
    public final OnListFragmentInteractionListener listener;
    public MovieItem item;

    public ViewHolder(View view, OnListFragmentInteractionListener listener) {
      super(view);
      this.view = view;
      this.movieSwitch = (Switch) view.findViewById(R.id.movie_switch);
      this.movieTitle = (TextView) view.findViewById(R.id.movie_title);
      this.movieImage = (ImageView) view.findViewById(R.id.imageView);
      this.listener = listener;
    }

    public void setSelected(boolean selected) {
      item.selected = selected;
      if (movieSwitch.isChecked() != selected) {
        movieSwitch.setChecked(selected);
      }
      if (null != listener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        listener.onItemSelectionChange(item);
      }
    }

    @Override
    public String toString() {
      return super.toString() + " '" + movieTitle.getText() + "'";
    }
  }
}
