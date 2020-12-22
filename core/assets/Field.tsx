<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.2" tiledversion="1.2.4" name="Field" tilewidth="32" tileheight="32" tilecount="19" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="1">
  <properties>
   <property name="TerrainType" value="forest"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="ForestTile.png"/>
 </tile>
 <tile id="2">
  <properties>
   <property name="TerrainType" value="plain"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="PlainTile.png"/>
 </tile>
 <tile id="6" type="Base">
  <properties>
   <property name="TerrainType" value="wall"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Base-1.png (1).png"/>
 </tile>
 <tile id="7" type="Base">
  <properties>
   <property name="TerrainType" value="wall"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Base-1.png (2).png"/>
 </tile>
 <tile id="8" type="Base">
  <properties>
   <property name="TerrainType" value="wall"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Base-1.png (3).png"/>
 </tile>
 <tile id="9" type="Base">
  <properties>
   <property name="TerrainType" value="base"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="Base-1.png (4).png"/>
 </tile>
 <tile id="10" type="Base">
  <properties>
   <property name="TerrainType" value="wall"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Base-1.png (5).png"/>
 </tile>
 <tile id="11" type="Base">
  <properties>
   <property name="TerrainType" value="wall"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Base-1.png.png"/>
 </tile>
 <tile id="12" type="Water">
  <properties>
   <property name="TerrainType" value="water"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="WaterTile-1.png.png"/>
  <animation>
   <frame tileid="12" duration="350"/>
   <frame tileid="13" duration="350"/>
   <frame tileid="14" duration="350"/>
   <frame tileid="13" duration="350"/>
  </animation>
 </tile>
 <tile id="13" type="Water">
  <properties>
   <property name="TerrainType" value="water"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="WaterTile-2.png (1).png"/>
 </tile>
 <tile id="14" type="Water">
  <properties>
   <property name="TerrainType" value="water"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="WaterTile-3.png.png"/>
 </tile>
 <tile id="15">
  <properties>
   <property name="TerrainType" value="air"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="AirObject-1.png.png"/>
 </tile>
 <tile id="16">
  <properties>
   <property name="TerrainType" value="Rune"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="Rune.png"/>
 </tile>
 <tile id="17">
  <properties>
   <property name="TerrainType" value="Trap"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="TotemTile-1.png.png"/>
 </tile>
 <tile id="18">
  <properties>
   <property name="TerrainType" value="Trap"/>
   <property name="blocked" type="bool" value="true"/>
  </properties>
  <image width="32" height="32" source="Wall.png"/>
 </tile>
 <tile id="19">
  <properties>
   <property name="TerrainType" value="Rune"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="RuneEffects-1.png.png"/>
  <animation>
   <frame tileid="19" duration="350"/>
   <frame tileid="20" duration="350"/>
   <frame tileid="21" duration="350"/>
  </animation>
 </tile>
 <tile id="20">
  <image width="32" height="32" source="RuneEffects-2.png.png"/>
 </tile>
 <tile id="21">
  <image width="32" height="32" source="RuneEffects-3.png.png"/>
 </tile>
 <tile id="22">
  <properties>
   <property name="TerrainType" value="Trap"/>
   <property name="blocked" type="bool" value="false"/>
  </properties>
  <image width="32" height="32" source="Beacon.png"/>
 </tile>
</tileset>
