package egtec.com.mylocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseManager extends SQLiteOpenHelper {

    private String createTableSQL = null;
    private String dropTableSQL = null;

    public DataBaseManager(Context context, String dataBaseName, int version, String createTableSQL, String dropTableSQL) {
        super(context, dataBaseName, null, version);

        this.createTableSQL = createTableSQL;
        this.dropTableSQL = dropTableSQL;
    }

    public DataBaseManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArrayList<String> sqls = new ArrayList<>();

        sqls.add("create table passeios(id integer primary key autoincrement, nome text not null, descricao text, locais text);");
        sqls.add("create table alertas(id integer primary key autoincrement, id_rota int, id_categoria int, foto text);");

        sqls.add("create table categorias (id integer primary key autoincrement, descricao text);");
        sqls.add("INSERT INTO categorias VALUES (1, 'Lixo')");
        sqls.add("INSERT INTO categorias VALUES (2, 'Cocô')");
        sqls.add("INSERT INTO categorias VALUES (3, 'Bituca')");
        sqls.add("INSERT INTO categorias VALUES (4, 'Tráfico')");
        sqls.add("INSERT INTO categorias VALUES (5, 'Carro Abandonado')");
        sqls.add("INSERT INTO categorias VALUES (6, 'Outros')");

        for (int i = 0; i < sqls.size(); i++) {
            db.execSQL(sqls.get(i));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("create table agenda2(id integer primary key autoincrement, nome text not null, fone text, endereco text not null);");
        //onCreate(db);
    }
}
