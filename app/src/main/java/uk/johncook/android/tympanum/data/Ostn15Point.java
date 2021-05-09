package uk.johncook.android.tympanum.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ostn15_shifts", indices = {@Index("Point_ID")})
public class Ostn15Point {
    @PrimaryKey
    @ColumnInfo(name = "Point_ID")
    public int pointId;

    @ColumnInfo(name = "ETRS89_Easting")
    public int easting;

    @ColumnInfo(name = "ETRS89_Northing")
    public int northing;

    @ColumnInfo(name = "ETRS89_OSGB36_EShift")
    public double eastShift;

    @ColumnInfo(name = "ETRS89_OSGB36_NShift")
    public double northShift;

    @ColumnInfo(name = "ETRS89_ODN_HeightShift")
    public double heightShift;

    @ColumnInfo(name = "Height_Datum_Flag")
    public int heightDatumFlag;
}
