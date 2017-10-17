package com.nse.model.response;

import java.util.List;

/**
 * Created by srikanth on 17/10/17.
 */
public class AnnonuncementsResult {
    List<Annonuncements> rows;
    boolean success;
    int results;

    public List<Annonuncements> getRows() {
        return rows;
    }

    public void setRows(List<Annonuncements> rows) {
        this.rows = rows;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "AnnonuncementsResult{" +
                "rows=" + rows +
                ", success=" + success +
                ", results=" + results +
                '}';
    }
}
