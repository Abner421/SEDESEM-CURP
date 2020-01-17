package com.example.sedesem.BaseDatos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sedesem.R;

import java.util.List;

public class NameAdapter extends ArrayAdapter<Name> {
    //storing all the names in the list
    private List<Name> names;
    private List<Nombre> nombres;
    private List<ApPat> ApPats;
    private List<ApMat> ApMats;

    //context object
    private Context context;

    //constructor
    public NameAdapter(Context context, int resource, List<Name> names, List<Nombre> nombres,
                       List<ApPat> ApPats, List<ApMat> ApMats) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
        this.nombres = nombres;
        this.ApPats = ApPats;
        this.ApMats = ApMats;
    }

    public NameAdapter(Context context, int resource, List<Name> names) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.names, null, true);
        TableRow tabla = listViewItem.findViewById(R.id.tablaPedorra);
        TextView textViewName = listViewItem.findViewById(R.id.txtViewCURP);
        //Añadidos
        TextView textViewApPat = listViewItem.findViewById(R.id.txtViewApPat);
        TextView textViewApMat = listViewItem.findViewById(R.id.txtViewApMat);
        TextView textViewNombres = listViewItem.findViewById(R.id.txtViewNombres);
        //ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        Name name = names.get(position);
        //Añadidos
        Nombre nombre = nombres.get(position);
        ApPat apPat = ApPats.get(position);
        ApMat apMat = ApMats.get(position);


        //setting the name to textview
        textViewName.setText(name.getName());
        //Añadidos
        textViewNombres.setText(nombre.getNombre());
        textViewApPat.setText(apPat.getApPat());
        textViewApMat.setText(apMat.getApMat());


        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (name.getStatus() == 0)
            //imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
            Toast.makeText(context, "", Toast.LENGTH_SHORT);
        else
            //imageViewStatus.setBackgroundResource(R.drawable.success);
            Toast.makeText(context, "", Toast.LENGTH_SHORT);

        return listViewItem;
    }
}
