package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.layout.helpers.RoadCell;

import java.util.Map;

public class WarehouseLayout {
    private final int sizeX;
    private final int sizeY;

    private final Map<String, RoadCell> cellMap;

    public WarehouseLayout(int sizeX, int sizeY, Map<String, RoadCell> cellMap){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.cellMap = cellMap;
    }


}
