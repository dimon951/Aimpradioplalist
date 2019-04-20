package dmitriy.deomin.aimpradioplalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Scogun
 * Date: 27.11.13
 * Time: 10:47
 * https://habr.com/post/203884/
 */
public class OpenFileDialog extends AlertDialog.Builder {

    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    //какаха небольшая
    private String currentPathHome = Environment.getExternalStorageDirectory().getPath();

    //показ кнопок
    private Boolean enebled_button;

    private List<File> files = new ArrayList<File>();
    private TextView title;
    private ListView listView;
    private FilenameFilter filenameFilter;
    private int selectedIndex = -1;
    private OpenDialogListener listener;
    private Drawable folderIcon;
    private Drawable fileIcon;
    private String accessDeniedMessage;
    private boolean isOnlyFoldersFilter;

    public interface OpenDialogListener {
        void OnSelectedFile(String fileName);
    }

    private class FileAdapter extends ArrayAdapter<File> {

        public FileAdapter(Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            if (view != null) {
                view.setText(file.getName());
                if (file.isDirectory()) {
                    setDrawable(view, folderIcon);
                } else {
                    setDrawable(view, fileIcon);
                    if (selectedIndex == position)
                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    else
                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
            }

            return view;
        }

        private void setDrawable(TextView view, Drawable drawable) {
            if (view != null) {
                if (drawable != null) {
                    drawable.setBounds(0, 0, 60, 60);
                    view.setCompoundDrawables(drawable, null, null, null);
                } else {
                    view.setCompoundDrawables(null, null, null, null);
                }
            }
        }
    }

    public OpenFileDialog(Context context) {
        super(context);
        isOnlyFoldersFilter = false;
        enebled_button=true;
        title = createTitle(context);
        changeTitle();
        LinearLayout linearLayout = createMainLayout(context);
        linearLayout.addView(createBackItem(context));
        listView = createListView(context);
        linearLayout.addView(listView);
        //linearLayout.setBackgroundColor(Main.Companion.getCOLOR_FON());
        setCustomTitle(title).setView(linearLayout);
    }

    @Override
    public AlertDialog show() {
        files.addAll(getFiles(currentPath));
        listView.setAdapter(new FileAdapter(getContext(), files));
        if (enebled_button) {
            setCustomTitle(title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (selectedIndex > -1 && listener != null) {
                        listener.OnSelectedFile(listView.getItemAtPosition(selectedIndex).toString());
                    }
                    if (listener != null && isOnlyFoldersFilter) {
                        listener.OnSelectedFile(currentPath);
                    }
                }
            })
                    .setNegativeButton(android.R.string.cancel, null);
        }
        return super.show();
    }

    public OpenFileDialog setFilter(final String filter) {
        filenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String fileName) {
                File tempFile = new File(String.format("%s/%s", file.getPath(), fileName));
                if (tempFile.isFile())
                    return tempFile.getName().matches(filter) || tempFile.getName().matches(".*\\.m3u8")||tempFile.getName().matches(".*\\.txt");
                return true;
            }
        };
        return this;
    }

    public OpenFileDialog setOnlyFoldersFilter() {
        isOnlyFoldersFilter = true;
        filenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String fileName) {
                File tempFile = new File(String.format("%s/%s", file.getPath(), fileName));
                return tempFile.isDirectory();
            }
        };
        return this;
    }

    public OpenFileDialog setOpenDialogListener(OpenDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public OpenFileDialog setFolderIcon(Drawable drawable) {
        this.folderIcon = drawable;
        return this;
    }

    public OpenFileDialog setFileIcon(Drawable drawable) {
        this.fileIcon = drawable;
        return this;
    }

    public OpenFileDialog setAccessDeniedMessage(String message) {
        this.accessDeniedMessage = message;
        return this;
    }

    public OpenFileDialog setEnablButton(Boolean v) {
        this.enebled_button = v;
        return this;
    }

    //изменение начальной директории
    public OpenFileDialog setStartDirectory(String dir) {
        this.currentPath = dir;
        return this;
    }

    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private static Point getScreenSize(Context context) {
        Point screeSize = new Point();
        getDefaultDisplay(context).getSize(screeSize);
        return screeSize;
    }

    private static int getLinearLayoutMinHeight(Context context) {
        return getScreenSize(context).y;
    }

    private LinearLayout createMainLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getLinearLayoutMinHeight(context));
        return linearLayout;
    }

    private int getItemHeight(Context context) {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeightSmall, value, true);
        getDefaultDisplay(context).getMetrics(metrics);
        return (int) TypedValue.complexToDimension(value.data, metrics);
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
        int itemHeight = getItemHeight(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        return textView;
    }

    private TextView createTitle(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        return textView;
    }

    private TextView createBackItem(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_Small);
        Drawable drawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_revert);
        drawable.setBounds(0, 0, 70, 70);
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(currentPath);
                File parentDirectory = file.getParentFile();
                if (parentDirectory != null) {
                    currentPath = parentDirectory.getPath();
                    RebuildFiles(((FileAdapter) listView.getAdapter()));
                }
            }
        });
        return textView;
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }

    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize(getContext()).x;
        int maxWidth = (int) (screenWidth * 0.99);
        if (getTextWidth(titleText, title.getPaint()) > maxWidth) {
            while (getTextWidth("..." + titleText, title.getPaint()) > maxWidth) {
                int start = titleText.indexOf("/", 2);
                if (start > 0)
                    titleText = titleText.substring(start);
                else
                    titleText = titleText.substring(2);
            }
            title.setText("..." + titleText);
        } else {
            title.setText(titleText);
        }
    }

    private List<File> getFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] list = directory.listFiles(filenameFilter);
        if (list == null)
            list = new File[]{};
        List<File> fileList = Arrays.asList(list);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                if (file.isDirectory() && file2.isFile())
                    return -1;
                else if (file.isFile() && file2.isDirectory())
                    return 1;
                else
                    return file.getPath().compareTo(file2.getPath());
            }
        });
        return fileList;
    }

    private void RebuildFiles(ArrayAdapter<File> adapter) {
        try {
            //если ушли в попу вернёмся на старт
            if (currentPath.length() < 2) {
                currentPath = currentPathHome;
            }

            List<File> fileList = getFiles(currentPath);
            files.clear();
            selectedIndex = -1;
            files.addAll(fileList);
            adapter.notifyDataSetChanged();
            changeTitle();
        } catch (NullPointerException e) {
            String message = getContext().getResources().getString(android.R.string.unknownName);
            if (!accessDeniedMessage.equals(""))
                message = accessDeniedMessage;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private ListView createListView(Context context) {
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                final ArrayAdapter<File> adapter = (FileAdapter) adapterView.getAdapter();
                final File file = adapter.getItem(index);
                if (file.isDirectory()) {
                    currentPath = file.getPath();
                    RebuildFiles(adapter);
                } else {
                    //если открыли в режиме удаления то при клике на файле будем предлогать удать
                    if(!enebled_button){
                        //покажем диалог подтверждения удаления файла
                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Main.context, android.R.style.Theme_Holo));
                        final View content = LayoutInflater.from(Main.context).inflate(R.layout.dialog_delete_stancii, null);
                        builder.setView(content);

                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        TextView textView = content.findViewById(R.id.text_voprosa_del_stncii);
                        Button del = content.findViewById(R.id.button_dialog_delete);
                        Button del_no = content.findViewById(R.id.button_dialog_no);
                        textView.setMovementMethod(new ScrollingMovementMethod());


//                        File_function f = new File_function();
//                        String file_telo = "";
//                        //покажем имя файла и краткое содержание если это m3u файл
//                        try {
//                            file_telo = f.read(file.getAbsolutePath());
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        file_telo = file_telo.replace("#EXTM3U", "");
//                        file_telo = file_telo.replace("#EXTINF:-1,", "");

                        final String name = file.getName();
                        String info = "Удалить:" + name + " ?";
                        textView.setText(info);


                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (file.delete()) {
                                    Toast.makeText(Main.context, "Удаленно", Toast.LENGTH_SHORT).show();

                                    if (name.equals("my_plalist.m3u")) {
                                        //пошлём сигнал пусть мой плейлист обновится
                                        Intent i = new Intent("Data_add");
                                        i.putExtra("update", "zaebis");
                                        Main.context.sendBroadcast(i);
                                    }

                                } else {
                                    Toast.makeText(Main.context, "Ошибка", Toast.LENGTH_SHORT).show();
                                }

                                RebuildFiles(adapter);

                                alertDialog.cancel();
                            }
                        });

                        del_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });



                        adapter.notifyDataSetChanged();
                    }else{
                        //иначе будем выделять или снимать
                        if (index != selectedIndex)
                            selectedIndex = index;
                        else
                            selectedIndex = -1;
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //долгим нажатием будем предлогать удалить
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id) {
                final ArrayAdapter<File> adapter = (FileAdapter) adapterView.getAdapter();
                final File file = adapter.getItem(index);
                if (file.isDirectory()) {
                    currentPath = file.getPath();
                    RebuildFiles(adapter);
                } else {
                    //покажем диалог подтверждения удаления файла
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Main.context, android.R.style.Theme_Holo));
                    final View content = LayoutInflater.from(Main.context).inflate(R.layout.dialog_delete_stancii, null);
                    builder.setView(content);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    TextView textView = content.findViewById(R.id.text_voprosa_del_stncii);
                    Button del = content.findViewById(R.id.button_dialog_delete);
                    Button del_no = content.findViewById(R.id.button_dialog_no);
                    textView.setMovementMethod(new ScrollingMovementMethod());


                    File_function f = new File_function();
                    String file_telo = "";
                    //покажем имя файла и краткое содержание если это m3u файл
                    try {
                        file_telo = f.read(file.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    file_telo = file_telo.replace("#EXTM3U", "");
                    file_telo = file_telo.replace("#EXTINF:-1,", "");

                    final String name = file.getName();
                    String info = "Удалить:" + name + "\nСодержимое:" + file_telo;
                    textView.setText(info);


                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (file.delete()) {
                                Toast.makeText(Main.context, "Удаленно", Toast.LENGTH_SHORT).show();

                                if (name.equals("my_plalist.m3u")) {
                                    //пошлём сигнал пусть мой плейлист обновится
                                    Intent i = new Intent("Data_add");
                                    i.putExtra("update", "zaebis");
                                    Main.context.sendBroadcast(i);
                                }

                            } else {
                                Toast.makeText(Main.context, "Ошибка", Toast.LENGTH_SHORT).show();
                            }

                            RebuildFiles(adapter);

                            alertDialog.cancel();
                        }
                    });

                    del_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                        }
                    });

            }
                return false;
            }
        });
        return listView;
    }

}
