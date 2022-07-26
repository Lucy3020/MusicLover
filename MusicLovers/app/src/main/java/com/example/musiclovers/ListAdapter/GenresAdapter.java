package com.example.musiclovers.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.Fragments.Genre.GenreDetail;
import com.example.musiclovers.Models.Genre;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder>{
    private Context context;
    int layoutHolder, name;
    ArrayList<Genre> genreItems;

    private ViewModel viewModel;

    public GenresAdapter(int layoutHolder, int name, ArrayList<Genre> genreItems, Context context) {
        this.context = context;
        this.name = name;
        this.genreItems = genreItems;
        this.layoutHolder = layoutHolder;
    }

    @NonNull
    @Override
    public GenresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutHolder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GenresAdapter.ViewHolder holder, int position) {
        Genre currentGenre = genreItems.get(position);
        holder.tvName.setText(currentGenre.getGenreType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel = new ViewModelProvider((MainActivity) context).get(ViewModel.class);
                viewModel.select(currentGenre);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new GenreDetail())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return genreItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(name);
        }
    }
}
