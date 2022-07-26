package com.example.musiclovers.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.Fragments.Playlist.PlaylistDetail;
import com.example.musiclovers.Models.Playlist;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    Context context;
    ArrayList<Playlist> bannerList;

    public BannerAdapter(Context context, ArrayList<Playlist> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    //định hình và gán dữ liệu cho mỗi object tượng trưng cho mỗi page
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view= inflater.inflate(R.layout.banner_format,null);

        ImageView backgroundImage = view.findViewById(R.id.banner_format_background_image);
        ImageView bannerImage = view.findViewById(R.id.banner_format_banner_image);
        TextView bannerThumbnail = view.findViewById(R.id.banner_format_thumbnail);
        TextView bannerDescription = view.findViewById(R.id.banner_format_description);
        backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        new DownloadImageTask(backgroundImage).execute("http://10.0.2.2:3000/"+ bannerList.get(position).getPlaylistImg());
        new DownloadImageTask(bannerImage).execute("http://10.0.2.2:3000/"+ bannerList.get(position).getPlaylistImg());
        bannerThumbnail.setText(bannerList.get(position).getPlaylistName());
        bannerDescription.setText("Top Picks This Week: " + bannerList.get(position).getPlaylistName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewModel viewModel = new ViewModelProvider((MainActivity)context).get(ViewModel.class);
                viewModel.select(bannerList.get(position));
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PlaylistDetail())//
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
