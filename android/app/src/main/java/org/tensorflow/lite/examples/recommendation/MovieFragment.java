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

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.lite.examples.recommendation.data.MovieItem;


public class MovieFragment extends Fragment {

  private static final String ARG_COLUMN_COUNT = "movie-fragment-column-count";
  private int columnCount = 1;
  private OnListFragmentInteractionListener listener;
  private RecyclerView recyclerView;
  private List<MovieItem> items = new ArrayList<>();


  public MovieFragment() {}

  @SuppressWarnings("unused")
  public static MovieFragment newInstance(int columnCount) {
    MovieFragment fragment = new MovieFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_COLUMN_COUNT, columnCount);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.selection_list, container, false);

    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      recyclerView = (RecyclerView) view;
      recyclerView.setHasFixedSize(true);
      recyclerView.setItemViewCacheSize(200);
      recyclerView.setDrawingCacheEnabled(true);
      recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
      if (columnCount <= 1) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
      } else {
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
      }
      recyclerView.setAdapter(new MovieRecyclerViewAdapter(items, listener));
    }

    return view;
  }

  public void setMovies(List<MovieItem> movies) {
    this.items = movies;
    recyclerView.setAdapter(new MovieRecyclerViewAdapter(this.items, listener));
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnListFragmentInteractionListener) {
      listener = (OnListFragmentInteractionListener) context;
    } else {
      throw new IllegalStateException(
              context + " must implement OnListFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  public interface OnListFragmentInteractionListener {
    void onItemSelectionChange(MovieItem item);
  }
}
