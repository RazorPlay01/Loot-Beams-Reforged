# Stonecutter: convenciones del proyecto

Guia de la sintaxis de Stonecutter aplicada a **LootBeams Continued** (multi-version + multi-loader).
Resumen de la wiki oficial: *Swaps*, *Conditions*, *Scopes*.

## 1. Condiciones

```java
//? if >= 26.2 {
...
//?} elif >= 26.1.2 {
...
//?} else if <= 1.21.1 {
...
//?} else {
...
//?}
```

- `//?` + `if` (o `elif` / `else if` / `else`) y el ambito (`{ }`).
- El *predicado* por defecto es la version del juego del proyecto (`versions/<v>`). Se puede
  escribir de forma explicita con `minecraft: >= 1.20`, o contra un *target* propio
  (`mod_menu: >= 1.0`).
- Operadores: `=`, `!=`, `<`, `>`, `<=`, `>=`, `~` (major+minor), `^` (major), y logicos
  `!`, `&&`, `||`, `( )`.
- Constantes y targets se declaran en el build script:

```kotlin
stonecutter {
    constants["snapshot"] = findProperty("is_snapshot") == "true"
    constants.match(property("mod_loader"), "fabric", "neoforge", "forge")
    targets["mod_menu"] = property("deps.mod_menu") as String
}
```

## 2. Ambitos (scopes)

| Ambito | Sintaxis | Efecto |
|---|---|---|
| Cerrado | `//? if cond {` ... `//?}` | Multiples lineas |
| Linea | `//? if cond` (sin llaves) | Solo la **siguiente linea no vacia** |
| Lookup palabra | `//? if cond >>` | Hasta el siguiente espacio |
| Lookup cadena | `//? if cond >> 'txt'` | Hasta `txt` (excluido) |
| Lookup cadena+ | `//? if cond >>+ 'txt'` | Hasta `txt` (incluido) |

Para una unica sentencia, el ambito de linea es mas limpio que abrir/cerrar llaves:

```java
//? if <= 1.21.1
RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
```

## 3. Anidamiento (lo que mas se rompe)

Stonecutter alterna el estado de comentario del codigo. Para que un bloque **anidado** dentro de
otro bloque comentado no cierre el comentario del padre, el marcador cambia por nivel:

| Nivel | Apertura | Cierre |
|---|---|---|
| 1 | `/*` | `*/` |
| 2 | `/^` | `^/` |
| 3 | `/^1` | `1^/` (superindices para niveles mas profundos) |

Ejemplo (fichero dentro de un wrapper de plataforma: todo su contenido puede quedar comentado en
el arbol del otro loader):

```java
//? neoforge {
/*import net.neoforged.api.distmarker.Dist;      <- nivel 1 (cuerpo del wrapper)
...
    //? if >= 26.1.2 {
    /^minecraft.gameRenderer.lightmap(),          <- nivel 2 (frame anidado)
    ^///?} else {
    minecraft.gameRenderer.lightTexture(),
    //?}
*///?}
```

### Reglas practicas (errores ya cometidos en este repo)

1. **Nunca** usar `/* ... */` para un frame que puede quedar dentro de un frame comentado: el `*/`
   cierra el padre y deja codigo "vivo" que rompe el arbol del otro loader
   (caso real: `NeoforgeClientEventSubscriber` rompio los arboles **fabric**).
   Frame dentro de un wrapper de plataforma -> usar `/^ ... ^/`.
2. La **primera linea del cuerpo de una rama debe ser codigo**. Stonecutter descomenta eliminando
   el marcador de comentario de cada linea: un `// comentario` como primera linea se convierte en
   texto invalido. Los comentarios van antes del `//? if` o despues de la primera linea de codigo.
3. **Nada de javadoc `/ ** ... * /` dentro de una rama**: su `*/` interactua con el comentario que
   Stonecutter anade. Usar `//`.
4. No dejar marcadores `/*` / `*/` sueltos a mitad de un cuerpo: re-comentan el resto de la rama
   en el arbol activo (el inactivo los tolera, el activo no).
5. El **cuerpo del wrapper de plataforma** debe ser plano (como `FabricClientSetup`) o
   pre-comentado con un unico `/* ... *///?}` (como los ficheros `neoforge`). Ambos funcionan, pero
   con el pre-comentado la regla 1 es obligatoria.

## 4. Swaps (`//$`)

Sustituyen un fragmento por un texto definido en el build script (evita repetir la misma condicion
en muchos sitios):

```java
public static void example() {
    //$ my_swap
    method1();
}
```

```kotlin
stonecutter {
    swaps["my_swap"] = when {
        current.parsed >= "1.21" -> "method1($1, $2);"
        else -> "method2($2, $1);"
    }
}
```

- Con parametros: `//$ my_swap arg '"cadena"'` y placeholders `$1`, `$2`, ...
- El ambito por defecto sustituye la(s) linea(s) siguientes; con `>>` se limita el texto capturado.
- Existe la forma local en linea: `//$ if >=1.21 '.method1()' else '\n.method2()'`.

Candidatos en este proyecto (pendiente, no bloqueante): accesor de *partial tick*
(`getTimer()` vs `getDeltaTracker()`), posicion de camara (`getPosition()` vs `position()`),
lightmap (`lightTexture()` vs `lightmap()`) y pantalla activa
(`client.screen` vs `client.gui.screen()`).

## 5. Replacements

`build.fabric.gradle.kts` y `build.neoforge.gradle.kts` usan
`stonecutter { replacements.string { ... } }` para renombrados inequivocos presentes en todas las
versiones. **Cuidado**: Stonecutter aplica el reemplazo *inverso* en las versiones que no cumplen el
predicado y recorta puntuacion, asi que solo se usan para identificadores completos (actualmente
unicamente `GuiGraphics` -> `GuiGraphicsExtractor` desde 26.1.2). Para diferencias de API con
parentesis o comas, usar `//? if` en el propio codigo.

## 6. Build de la matriz

```powershell
.\gradlew.bat :1.21.1-fabric:compileJava :1.21.1-neoforge:compileJava `
  :1.21.11-fabric:compileJava :1.21.11-neoforge:compileJava `
  :26.1.2-fabric:compileJava :26.1.2-neoforge:compileJava `
  :26.2-fabric:compileJava :26.2-neoforge:compileJava `
  --rerun-tasks --continue --no-configuration-cache --console=plain
```

- `--continue` es imprescindible para ver los errores de **todos** los objetivos en una pasada.
- Usar `--no-configuration-cache`: con la cache invalidada, la fase *Calculating task graph* se queda
  colgada varios minutos (daemon al 100 % de CPU sin avanzar).
- Ante errores de **sintaxis** (`class, interface, enum, or record expected`) javac aborta la fase de
  atribucion: los demas ficheros no reportan nada. Arreglar primero los de sintaxis y recompilar.
- Los arboles generados estan en `versions/<version>-<loader>/build/generated/stonecutter/main/java`;
  comparar ahi el crudo y el generado es la forma mas rapida de depurar un marcador mal puesto.

**Aviso importante sobre marcadores inline**: la forma `matrixStack/*? if >= 26.3 {*/.rotate(...)` **NO la
procesa** esta version de Stonecutter: los marcadores se quedan como comentario y se compila la rama "if"
en todas las versiones (rompio las 5 versiones a la vez). Para diferencias dentro de una sentencia, usar
frames de linea (`//? if >= 26.3 { /*stmt*/ //?} else { stmt //?}`) o extraer un helper a una clase
compartida con frames de nivel 0.

