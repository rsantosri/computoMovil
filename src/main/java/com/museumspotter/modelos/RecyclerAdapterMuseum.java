package com.museumspotter.modelos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.museumspotter.DetalleMuseo;
import com.museumspotter.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RecyclerAdapterMuseum extends RecyclerView.Adapter<RecyclerAdapterMuseum.MyHolder> {

    List<Museum> list;
    Context context;

    public RecyclerAdapterMuseum(List<Museum> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapterMuseum.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.museum_rows, viewGroup, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    Button onclick;
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterMuseum.MyHolder myHolder, int i) {
        Museum mylist = list.get(i);
        myHolder.id.setText("" + mylist.getId());
        myHolder.nombre.setText(mylist.getNombre());
        myHolder.direccion.setText(mylist.getDireccion());
        myHolder.calificacion.setText(""+mylist.getCalificacion());
        myHolder.button.setText("Ver más");


    }

    @Override
    public int getItemCount() {
        int arr = 0;

        try {
            if (list.size() == 0) {

                arr = 0;

            } else {

                arr = list.size();
            }

        } catch (Exception e) {


        }

        return arr;

    }




    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView id, nombre, direccion, calificacion;

        Button button;


        public MyHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idMuseum);
            nombre = (TextView) itemView.findViewById(R.id.nombreMuseum);
            direccion = (TextView) itemView.findViewById(R.id.direccionMuseum);
            calificacion = (TextView) itemView.findViewById(R.id.calificacionMuseum);
            button = (Button) itemView.findViewById(R.id.btnMuseum);
            context = itemView.getContext();
            button.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnMuseum){
                Intent intent = new Intent(context, DetalleMuseo.class);
                intent.putExtra("id",id.getText());
                context.startActivity(intent);
            }
        }
    }


    private class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){
                e.printStackTrace();
            }
            return logo;
        }


        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}


