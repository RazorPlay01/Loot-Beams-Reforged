package com.github.razorplay.lootbeams.extensions;

public interface LootbeamsBufferBuilder {
   /**
    * Escribe el color base en un formato propio del mod (que nombra el atributo "Color0").
    * <p>
    * Hace falta porque a partir de 26.2 {@code BufferBuilder} solo resuelve atributos por las 8
    * semanticas canonicas ("Position", "Color", "UV0".."UV3", "Normal", "LineWidth"), asi que
    * {@code setColor(...)} no encuentra el elemento "Color0" y no escribia nada. Si el formato
    * usa el elemento vanilla "Color" (capas con {@code DefaultVertexFormat.PARTICLE}) se escribe
    * ese.
    */
   LootbeamsBufferBuilder color0(int var1, int var2, int var3, int var4);

   LootbeamsBufferBuilder color1(int var1, int var2, int var3, int var4);

   LootbeamsBufferBuilder uvCenter(float var1, float var2);

   LootbeamsBufferBuilder uvSize(float var1, float var2);

   LootbeamsBufferBuilder shortCustomData(float var1, float var2);

   LootbeamsBufferBuilder longCustomData(float var1, float var2, float var3, float var4);
}
