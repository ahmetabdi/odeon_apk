package uk.co.odeon.androidapp.indexers;

import android.database.DataSetObserver;
import android.widget.SectionIndexer;

public class FilmListSingleSectionIndexer extends DataSetObserver implements SectionIndexer {
    public static final String SECTIONNAME_CINEMA_FILMS_ADVANCED = "Later at this cinema";
    public static final String SECTIONNAME_CINEMA_FILMS_CURRENT = "Today at this cinema";
    public static final String SECTIONNAME_CINEMA_FILMS_NEXT = "Next 7 days at this cinema";
    public static final String SECTIONNAME_TOP_FILMS = "TOP FILMS NOW SHOWING";
    protected String sectionName;

    public FilmListSingleSectionIndexer() {
        this.sectionName = "";
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getPositionForSection(int section) {
        return 0;
    }

    public int getSectionForPosition(int position) {
        return 0;
    }

    public Object[] getSections() {
        return new String[]{this.sectionName};
    }
}
