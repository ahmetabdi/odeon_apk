package uk.co.odeon.androidapp.indexers;

import android.util.Log;
import android.widget.SectionIndexer;
import java.util.HashMap;
import java.util.List;
import uk.co.odeon.androidapp.model.FilmListMyFilm;
import uk.co.odeon.androidapp.model.FilmListMyFilm.FilmCategory;

public class RewardsMyFilmsSectionIndexer implements SectionIndexer {
    private List<FilmListMyFilm> films;
    private HashMap<FilmCategory, Integer> startPositions;

    public RewardsMyFilmsSectionIndexer(List<FilmListMyFilm> films) {
        this.startPositions = new HashMap();
        setFilms(films);
    }

    public void setFilms(List<FilmListMyFilm> films) {
        this.films = films;
        index(true);
    }

    public void index(boolean force) {
        int i = 0;
        if (force || this.startPositions.isEmpty()) {
            this.startPositions.clear();
            this.startPositions.put(FilmCategory.values()[0], Integer.valueOf(0));
            int pos = 0;
            for (FilmListMyFilm f : this.films) {
                if (this.startPositions.containsKey(f.category)) {
                    pos++;
                } else {
                    this.startPositions.put(f.category, Integer.valueOf(pos));
                    pos++;
                }
            }
            FilmCategory[] values = FilmCategory.values();
            int length = values.length;
            while (i < length) {
                FilmCategory cat = values[i];
                if (!this.startPositions.containsKey(cat)) {
                    this.startPositions.put(cat, Integer.valueOf(999999));
                }
                Log.i("ix", cat + "=" + this.startPositions.get(cat));
                i++;
            }
        }
    }

    public int getPositionForSection(int section) {
        index(false);
        if (section >= FilmCategory.values().length) {
            return 999999;
        }
        return ((Integer) this.startPositions.get(FilmCategory.values()[section])).intValue();
    }

    public int getSectionForPosition(int position) {
        int i = 0;
        if (position == 0) {
            return 0;
        }
        index(false);
        FilmCategory lastCat = FilmCategory.values()[0];
        FilmCategory[] values = FilmCategory.values();
        int length = values.length;
        while (i < length) {
            FilmCategory cat = values[i];
            if (position < ((Integer) this.startPositions.get(cat)).intValue()) {
                return lastCat.ordinal();
            }
            lastCat = cat;
            i++;
        }
        return FilmCategory.values()[FilmCategory.values().length - 1].ordinal();
    }

    public Object[] getSections() {
        return FilmCategory.values();
    }
}
