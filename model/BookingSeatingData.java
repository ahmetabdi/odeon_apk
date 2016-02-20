package uk.co.odeon.androidapp.model;

import java.util.ArrayList;
import java.util.List;

public class BookingSeatingData {
    public int seatingPlanHeight;
    public int seatingPlanWidth;
    public List<BookingSection> sections;

    public BookingSeatingData() {
        this.sections = new ArrayList();
    }

    public BookingSection getSectionById(int id) {
        for (BookingSection section : this.sections) {
            if (section.id == id) {
                return section;
            }
        }
        return null;
    }

    public void selectSection(BookingSection section) {
        clearSectionSelection();
        section.selected = true;
    }

    public void clearSectionSelection() {
        for (BookingSection s : this.sections) {
            s.selected = false;
        }
    }

    public BookingSection getSelectedSection() {
        for (BookingSection s : this.sections) {
            if (s.selected) {
                return s;
            }
        }
        return null;
    }
}
