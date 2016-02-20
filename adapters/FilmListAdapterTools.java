package uk.co.odeon.androidapp.adapters;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.FilmListFilm;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class FilmListAdapterTools {
    protected static final String TAG;
    private Handler handler;
    public int loadingImageRes;
    public int unavailableImageRes;

    /* renamed from: uk.co.odeon.androidapp.adapters.FilmListAdapterTools.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ FilmListFilm val$film;

        AnonymousClass1(FilmListFilm filmListFilm) {
            this.val$film = filmListFilm;
        }

        public void onClick(View v) {
            Log.i(FilmListAdapterTools.TAG, "Film Image clicked: " + this.val$film.filmId + " / " + this.val$film.trailerUrl);
            if (FilmListAdapterTools.this.handler != null) {
                Message msg = new Message();
                msg.what = Constants.FILM_LIST_MSG_TRAILERCLICK;
                msg.arg1 = this.val$film.filmId.intValue();
                msg.obj = this.val$film.trailerUrl;
                FilmListAdapterTools.this.handler.sendMessage(msg);
            }
        }
    }

    public class ViewHolder {
        public ImageView audioDescribedView;
        public ImageView certView;
        public ImageView closedCaptureView;
        public ImageView filmImageBBFView;
        public ImageView filmImageView;
        public TextView filmListHeaderText;
        public TextView filmTitleTextView;
        public TextView genreView;
        public RatingBar ratingView;
        public TextView releaseDateView;
    }

    static {
        TAG = FilmListCursorAdapter.class.getSimpleName();
    }

    public FilmListAdapterTools(Handler handler) {
        this.loadingImageRes = R.drawable.film_listing_noimg;
        this.unavailableImageRes = R.drawable.film_listing_noimg;
        this.handler = handler;
    }

    public ViewHolder initViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.filmTitleTextView = (TextView) v.findViewById(R.id.film_list_title);
        vh.filmImageView = (ImageView) v.findViewById(R.id.film_list_image_small);
        vh.filmImageBBFView = (ImageView) v.findViewById(R.id.film_list_image_bbf);
        vh.genreView = (TextView) v.findViewById(R.id.film_list_genre);
        vh.releaseDateView = (TextView) v.findViewById(R.id.film_list_reldate);
        vh.certView = (ImageView) v.findViewById(R.id.film_list_cert);
        vh.ratingView = (RatingBar) v.findViewById(R.id.film_list_rating);
        vh.filmListHeaderText = (TextView) v.findViewById(R.id.list_row_header_text);
        vh.audioDescribedView = (ImageView) v.findViewById(R.id.film_list_audio_described);
        vh.closedCaptureView = (ImageView) v.findViewById(R.id.film_list_closed_capture);
        v.setTag(vh);
        return vh;
    }

    public void bindView(View view, ViewHolder vh, FilmListFilm film, SparseIntArray accessibleData) {
        Log.d(TAG, "bindView #" + film.filmId);
        vh.filmTitleTextView.setText(film.filmText + " ");
        boolean hasTrailer = film.trailerUrl != null;
        vh.filmImageView.setOnClickListener(new AnonymousClass1(film));
        DrawableManager.getInstance().loadDrawable(film.imageURL, vh.filmImageView, DrawableManager.getInstance().buildImageCacheFileBasedOnURLFilename(film.imageURL), this.loadingImageRes, this.unavailableImageRes, hasTrailer ? Integer.valueOf(R.drawable.film_listing_trailer_overlay) : null, true);
        if (vh.filmImageBBFView != null) {
            if (film.bbf) {
                vh.filmImageBBFView.setVisibility(0);
            } else {
                vh.filmImageBBFView.setVisibility(8);
            }
        }
        vh.certView.setImageResource(ODEONApplication.getInstance().certStringToImageResource(film.cert));
        if (film.rateable) {
            vh.ratingView.setRating(film.halfrating / 2.0f);
            vh.ratingView.setVisibility(0);
        } else {
            vh.ratingView.setVisibility(4);
        }
        if (film.genreText == null || film.genreText.equalsIgnoreCase("null")) {
            vh.genreView.setText("");
        } else {
            vh.genreView.setText(film.genreText);
        }
        vh.releaseDateView.setText(film.relDateText);
        int audioDescribedVisability = 4;
        int closedCaptureVisability = 4;
        if (accessibleData != null) {
            if (accessibleData.indexOfKey(film.filmId.intValue()) >= 0) {
                int accessibleDataPerFilmId = accessibleData.get(film.filmId.intValue());
                if ((accessibleDataPerFilmId & 1) == 1) {
                    audioDescribedVisability = 0;
                }
                if ((accessibleDataPerFilmId & 2) == 2) {
                    closedCaptureVisability = 0;
                }
            }
        }
        if (vh.audioDescribedView != null) {
            vh.audioDescribedView.setVisibility(audioDescribedVisability);
        }
        if (vh.closedCaptureView != null) {
            vh.closedCaptureView.setVisibility(closedCaptureVisability);
        }
    }
}
