package com.sunilson.firenote.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.Interfaces.ChecklistInterface;
import com.sunilson.firenote.R;
import com.sunilson.firenote.baseClasses.ChecklistElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Linus Weiss
 */

public class ImportFromTextDialog extends SuperDialog {

    private ChecklistInterface checklistInterface;
    boolean recipeToggle = false;
    private LinearLayout recipeContainer;
    private EditText editText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (activity instanceof ChecklistInterface) {
            checklistInterface = (ChecklistInterface) activity;
        }

        titleText.setText(getContext().getString(R.string.import_from_textfile));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_import_text, null);
        LinearLayout recipe = (LinearLayout) content.findViewById(R.id.format_recipe);
        LinearLayout recipeInfo = (LinearLayout) content.findViewById(R.id.format_recipe_info);
        recipeContainer = (LinearLayout) content.findViewById(R.id.format_recipe_holder);
        recipe.setOnClickListener(initializeRecipeListener());
        recipeInfo.setOnClickListener(initializeRecipeInfoListener());
        editText = (EditText) content.findViewById(R.id.import_text);
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int counter = 0;
                Scanner scanner = new Scanner(editText.getText().toString());
                while (scanner.hasNextLine()) {
                    if (counter < 20) {
                        if (checklistInterface != null) {
                            String next = scanner.nextLine();
                            if (next != null && !next.isEmpty()) {
                                DatabaseReference dRef = checklistInterface.getElementsReference().push();
                                ChecklistElement checklistElement = new ChecklistElement(next.trim());
                                if (next.charAt(0) == '*' || next.charAt(0) == '☒') {
                                    checklistElement.setFinished(true);
                                    checklistElement.setText(next.substring(1).trim());
                                } else if (next.charAt(0) == '☐') {
                                    checklistElement.setText(next.substring(1).trim());
                                }
                                dRef.setValue(checklistElement);
                            }
                        }
                    } else {
                        break;
                    }
                    counter++;
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    public static ImportFromTextDialog newInstance() {
        return new ImportFromTextDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener initializeRecipeInfoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!recipeToggle) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recipeContainer.getLayoutParams();
                    params.height *= 2;
                    recipeContainer.setLayoutParams(params);
                    ((TextView)recipeContainer.findViewById(R.id.recipe_text)).setText(getString(R.string.format_recipe_info));
                    ((ImageView)recipeContainer.findViewById(R.id.recipe_image)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancel_white_24dp));
                    recipeToggle = true;
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recipeContainer.getLayoutParams();
                    params.height /= 2;
                    recipeContainer.setLayoutParams(params);
                    ((TextView)recipeContainer.findViewById(R.id.recipe_text)).setText(getString(R.string.format_recipe));
                    ((ImageView)recipeContainer.findViewById(R.id.recipe_image)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_white_24dp));
                    recipeToggle = false;
                }
            }
        };
    }

    private View.OnClickListener initializeRecipeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                editText.setText(formatRecipe(text));
            }
        };
    }

    private String formatRecipe(String input) {
        String result = "";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input.trim()));

        int firstPos = 0;
        boolean numberSequence = false;
        List<String> lines = new ArrayList<>();
        boolean bracesStarted = false;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    firstPos = 0;
                    //Replace Fractions
                    line = line.replaceAll("¼", " 1/4 ");
                    line = line.replaceAll("½", " 1/2 ");
                    line = line.replaceAll("¾", " 3/4 ");
                    line = line.replaceAll("⅛", " 1/8 ");
                    line = line.replaceAll("¾", " 3/4 ");

                    //Replace special characters in front of line
                    line = line.replaceAll("^\\W+", "");

                    //Replace all special characters
                    line = line.replaceAll("[^A-Za-zöÖäÄüÜß\\-,;\\)\\(\\\\.\\d\\s\\*!/]", "");

                    //Add blank spaces to common metrics german
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(m|M){1}l(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(g|G){1}(?=[A-Z][^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(kg|Kg|KG){1}(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))EL(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))TL(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(P|p){1}ack(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(P|p){1}rise(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(S|s){1}tück(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(S|s){1}tk(?=.+[^\\s]))", "$1 ");

                    //Same for US metrics
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(c|C){1}up[s]?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(t|T){1}ablespoon[s]?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(g|G){1}ram[s]?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(i|I){1}nch[es]?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(p|P){1}inch[es]?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(t|T){1}bsp?(?=.+[^\\s]))", "$1 ");
                    line = line.replaceAll("((?<=(\\d{1,5})|(\\s))(t|T){1}sp?(?=.+[^\\s]))", "$1 ");

                    //Remove double breaks
                    line = line.replaceAll("[\r\n]+", "\n");
                    for (int i = 0; i < line.length(); i++) {
                        if((int)line.charAt(i) < 1 || (int)line.charAt(i) > 254 ){
                            //Remove invalid character
                            String beforeString = line.substring(0, i);
                            String afterString = line.substring(i+1);
                            line = beforeString + afterString;
                            i--;
                        } else {
                            //Check if char is a digit
                            if (Character.isDigit(line.charAt(i))) {
                                if (!numberSequence && !bracesStarted) {
                                    //End current line and set new firstpos
                                    numberSequence = true;
                                    if (i != 0) {
                                        lines.add(line.substring(firstPos, i));
                                        firstPos = i;
                                    }
                                }
                            } else {
                                //Check for number seperators, if not end number sequence
                                if (line.charAt(i) != '.' && line.charAt(i) != ',' && line.charAt(i) != '/' && line.charAt(i) != ' ') {
                                    //End number sequence
                                    numberSequence = false;
                                }

                                if (i != 0) {
                                    //Add blank after numberSequence if not in braces
                                    if (!numberSequence && !bracesStarted && Character.isDigit(line.charAt(i - 1))) {
                                        line = line.substring(0, i) + " " + line.substring(i, line.length());
                                        i++;
                                    }
                                }

                                if (line.charAt(i) == '(') {
                                    bracesStarted = true;
                                } else if (line.charAt(i) == ')') {
                                    bracesStarted = false;
                                }
                            }
                        }

                        if (i == line.length() - 1) {
                            lines.add(line.substring(firstPos, line.length()));
                        }
                    }

                    /*
                    //Add blank space after numbers (positive lookahead)
                    line = line.replaceAll("(\\d+(?=[öÖäÄüÜßA-Za-z]))", "$1 ");
                    //Add Line break before numbers
                    line = line.replaceAll("(\\d+(.\\d+)?)", "\n$1");
                    //Remove Line break inside brackets
                    line = line.replaceAll("\\([\r\n]", "\\(");
                    //Remove Line break after slash
                    line = line.replaceAll("/[\r\n]", "/");
                    //Remove white space in front and end
                    line = line.trim();
                    //Add Line break after Line (got removed by trim())
                    result += line + "\n";
                    */
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String string : lines) {
                string = string.replaceAll("\\s\\s+", " ");
                stringBuilder.append(string);
                stringBuilder.append("\n");
            }

            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
