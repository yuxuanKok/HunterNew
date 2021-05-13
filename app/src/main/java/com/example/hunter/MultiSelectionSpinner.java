package com.example.hunter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectionSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {
    ArrayList<Category> categories = null;
    boolean[] selection = null;
    ArrayAdapter adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }


    @Override
    public void onClick(DialogInterface dialog, int idx, boolean isChecked) {
        if (selection != null && idx < selection.length) {
            selection[idx] = isChecked;

            adapter.clear();
            adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "'idx' is out of bounds.");
        }
    }


    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] itemNames = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            itemNames[i] = categories.get(i).getName();
        }

        builder.setMultiChoiceItems(itemNames, selection, this);

        builder.setPositiveButton("OK", (arg0, arg1) -> {
            // Do nothing
        });

        builder.show();

        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }


    public void setItems(ArrayList<Category> categories) {
        this.categories = categories;
        selection = new boolean[this.categories.size()];
        adapter.clear();
        adapter.add("Choose Category");
        Arrays.fill(selection, false);
    }


    public void setSelection(ArrayList<Category> selection) {
        for (int i = 0; i < this.selection.length; i++) {
            this.selection[i] = false;
        }

        for (Category sel : selection) {
            for (int j = 0; j < categories.size(); ++j) {
                if (categories.get(j).getName().equals(sel.getName())) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear();
        adapter.add(buildSelectedItemString());
    }


    public ArrayList<Category> getSelectedItems() {
        ArrayList<Category> selectedItems = new ArrayList<>();

        for (int i = 0; i < categories.size(); ++i) {
            if (selection[i]) {
                selectedItems.add(categories.get(i));
            }
        }

        return selectedItems;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < categories.size(); ++i) {
            if (selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }

                foundOne = true;

                sb.append(categories.get(i).getName());
            }
        }

        return sb.toString();
    }

}
