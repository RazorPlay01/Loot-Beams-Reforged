# Guía completa de Stonecutter

> Documento consolidado que reúne la documentación oficial de Stonecutter en un único archivo.
> Fuente: los 4 PDF de `` (Conditions, Scopes, Replacements y Swaps) de https://stonecutter.kikugie.dev.

## Índice

1. [¿Qué es Stonecutter?](#1-qué-es-stonecutter)
2. [Conceptos básicos: marcadores y comentarios](#2-conceptos-básicos-marcadores-y-comentarios)
3. [Alcances (Scopes)](#3-alcances-scopes)
4. [Condiciones (Conditions)](#4-condiciones-conditions)
5. [Swaps](#5-swaps)
6. [Replacements](#6-replacements)
7. [Cómo se usa en este proyecto (LootBeanStonecutter)](#7-cómo-se-usa-en-este-proyecto-lootbeanstonecutter)

---

## 1. ¿Qué es Stonecutter?

Stonecutter es una herramienta de [KikuGie](https://stonecutter.kikugie.dev/) que permite mantener **una sola base de código** para múltiples versiones de Minecraft y múltiples cargadores de mods (Fabric, NeoForge, Forge).

Funciona **procesando los archivos fuente antes de compilarlos**: lee comentarios especiales ("marcadores") y, según la versión/cargador activo, activa o desactiva fragmentos de código, sustituye valores o reemplaza texto.

Existen **tres tipos de sentencias**, cada una con su propio marcador dentro de un comentario:

| Tipo | Marcador | Propósito |
|------|----------|-----------|
| **Condiciones** | `//?` | Activa/desactiva bloques de código según una expresión booleana. |
| **Swaps** | `//$` | Inserta un valor definido condicionalmente en el build script. |
| **Replacements** | `//~` | Busca y reemplaza texto en el archivo. |

> **Versión usada en este proyecto:** Stonecutter `0.9.8` (ver `../settings.gradle.kts`).
>
> ⚠️ **Notas de versión importantes:**
> - Los **`targets`** se llamaban **`dependencies`** antes de la versión `0.10`. Fueron renombrados para evitar confusión con las dependencias de Gradle; por lo demás son funcionalmente idénticos.
> - Los **replacements locales con nombre y anónimos** **no están disponibles** en `0.9` y versiones anteriores (como la de este proyecto).

---

## 2. Conceptos básicos: marcadores y comentarios

Toda la magia de Stonecutter ocurre dentro de **comentarios de línea** de tu lenguaje (Java/Kotlin). El comentario empieza con el marcador correspondiente (`?`, `$` o `~`) y el resto es la "instrucción" que Stonecutter interpreta.

```java
//? if >=1.21 {
    methodX();
//?}
```

El bloque `{ ... }` (o una sola línea) es el **alcance** al que afecta la sentencia. Cuando la condición es verdadera, el contenido queda **habilitado**; cuando es falsa, queda **comentado** (y viceversa).

---

## 3. Alcances (Scopes)

Los **alcances** son marcadores al inicio o al final de una sentencia de comentario que definen **qué segmento del archivo se ve afectado**. Normalmente funcionan igual para los tres tipos de sentencia (condiciones, swaps y replacements).

Se asemejan a la sintaxis de los lenguajes tipo C (C, Java, Kotlin, Rust).

### 3.1. Alcance cerrado (Closed Scope)

Se marca con un par de llaves `{` y `}` y captura **varias líneas**:

```java
//? condition {
action1();
action2();
//?}
```

### 3.2. Alcance de línea (Line Scope)

Omite el marcador y afecta a la **siguiente línea no vacía**:

```java
//? condition
action1();   // ← esta línea se comentará o no
action2();   // ← no afectada
```

### 3.3. Alcance de búsqueda (Lookup Scope)

Se marca con el operador `>>` y captura texto hasta una coincidencia especificada. Tiene tres modos:

| Modo | Sintaxis | Comportamiento |
|------|----------|----------------|
| **Coincidencia de palabra** | `... >>` | Captura el siguiente segmento de texto, interrumpido por espacios en blanco. |
| **Coincidencia de cadena** | `... >> 'abc'` | Captura texto **hasta** la cadena indicada (sin incluirla). |
| **Coincidencia de cadena inclusiva** | `... >>+ 'abc'` | Captura texto **hasta e incluyendo** la cadena indicada. |

Ejemplos:

```java
//? condition >>        // palabra: captura "action1();"
action1(); action2();

//? condition >> 'action3'
action1();              // "action1(); action2();" se comentará
action2();
action3();

//? condition >>+ '2();'
action1();              // "action1(); action2();" se comentará (incluye el final)
action2();
action3();
```

---

## 4. Condiciones (Conditions)

Una **condición** evalúa una expresión booleana cuando se procesa el archivo y activa/desactiva el alcance asociado: habilita el código para las versiones que coinciden, o lo deja comentado en caso contrario.

### 4.1. Sintaxis de la sentencia

Una sentencia de condición empieza con el marcador `?`, seguido de la palabra clave `if` y una expresión:

```
//? if [expresión] (marcador de alcance)
```

Ejemplo básico:

```java
//? if is_snapshot {
MinecraftClass.snapshotOnlyMethod();
//?}
```

La expresión puede ser:
- una **constante**,
- un **predicado de versión**, o
- una **combinación** de ambos unidos con operadores lógicos.

Lo que afecta la condición viene determinado por su **alcance** (ver sección 3).

### 4.2. Constantes

Stonecutter permite definir **valores booleanos** para usarlos en la sintaxis de comentarios. Son muy habituales en configuraciones multi-loader o en builds de prueba.

**Uso de constantes** (referenciadas por nombre en los comentarios):

```java
//? if is_snapshot {
MinecraftClass.snapshotOnlyMethod();
//?}
```

**Definición de constantes**:

```kotlin
// build.gradle.kts
stonecutter {
    constants["snapshot"] = findProperty("is_snapshot") == "true"
}
```

> 💡 **Convención:** las constantes se nombran en `snake_case`.

**Selector de elección (Choice Selector):** un selector puede definir varias constantes en una sola expresión, lo que es útil en configuraciones multi-loader para crear constantes de cargador:

```kotlin
stonecutter {
    val loader = property("mod_loader") // neoforge
    constants.match(
        loader,
        "fabric",   // != "loader" -> false
        "neoforge", // == "loader" -> true
        "forge",    // != "loader" -> false
    )
}
```

### 4.3. Predicados

Los **predicados** definen restricciones sobre una versión usando comprobaciones de igualdad/desigualdad y algunas operaciones de SemVer.

Un predicado consiste en un **operador de comparación opcional** (como `>=` o `~`) y la **versión** contra la que se comprueba.

**Uso de predicados** (se escriben directamente en el comentario, comparando contra el objetivo implícito, normalmente la versión del juego indicada en `settings.gradle(.kts)`):

```java
//? if >=1.21
methodX();

//? if 1.21.11        // al comprobar una versión exacta, `=` se puede omitir
methodY();

//? if >=1.20 <1.21   // equivalente a `>=1.20 && <1.21`
methodZ();
```

Los predicados se pueden **encadenar** (varios predicados en una fila); **todos** deben cumplirse.

#### Tipos de versión

Stonecutter clasifica las versiones en dos tipos:

1. **Semántica (`SemanticVersion`):** sigue aproximadamente la especificación SemVer pero admite valores más flexibles:
   - El núcleo puede tener cualquier número de componentes, permitiendo versiones como `1.0.0.1`.
   - Al comparar versiones con distinto número de componentes, el resto se trata como ceros (p. ej. `1.2` es igual a `1.2.0`).
   - Los modificadores de pre-release y build **pueden contener guiones bajos**.

2. **De cadena (`StringVersion`):** versiones escritas como identificadores:
   - Deben empezar con un guion bajo o una letra; los siguientes caracteres pueden incluir dígitos.
   - Se comparan **lexicográficamente** contra el valor de la propiedad `Version.value`.
   - (Las versiones de cadena se comparan usando el orden de caracteres estándar, como el de una tabla ASCII.)

```kotlin
val semantic: SemanticVersion = SemanticVersion.parse("1.0")
val string: StringVersion = StringVersion.parse("release")

assert(semantic < string)              // Comparable<Version>
assert(semantic.value < string.value)  // "1.0" < "release"
```

#### Operadores de predicado

| Operador | Significado | Ejemplo |
|----------|-------------|---------|
| `=` | Igual (comprobación básica) | `=1.20` |
| `!=` | Distinto | `!=1.20` |
| `<` | Menor que | `<1.21` |
| `>` | Mayor que | `>1.20` |
| `<=` | Menor o igual | `<=1.20` |
| `>=` | Mayor o igual | `>=1.20` |
| `~` | Versión mayor y menor iguales | `1.2.3 ~1.2.1` |
| `^` | Versión mayor igual | `1.2.3 ^1.4` |
| *(sin operador)* | Igual que `=` | `if 1.20` en vez de `if =1.20` |

> ⚠️ `~` y `^` (y el operador omitido) **solo están disponibles para versiones semánticas**. Las comprobaciones de versión de cadena deben llevar un operador de comparación.

**Predicados SemVer NO soportados:**

| Predicado | Motivo |
|-----------|--------|
| `x` (como en `1.20.x`) | Se puede sustituir por `~` o `^` en la mayoría de casos. |
| `*` ("cualquiera") | No es útil para Stonecutter: esa condición siempre tendría éxito y debería eliminarse. |

### 4.4. Targets (objetivos)

Al escribir `? if >=1.20`, el **objetivo del predicado es implícito**: se usa la versión del juego indicada en `settings.gradle(.kts)`.

Puedes escribir esta comprobación de forma explícita con el operador de asignación `:`:

```
? if minecraft: >=1.20
```

(que en este caso produce el mismo resultado).

Un objetivo se selecciona con el operador de asignación (`:`) antes del predicado:

```java
//? if mod_menu: >=1.0 {
public class ModMenuIntegration {
}
//?}
```

**Definición de targets** (también se pueden añadir targets personalizados, útiles para partes del código que dependen de librerías de terceros):

```kotlin
// build.gradle.kts
stonecutter {
    targets["mod_menu"] = property("deps.mod_menu") as String
}
```

> 💡 **Convención:** los targets se nombran en `snake_case`. Las versiones proporcionadas se analizan como se describe en la sección de tipos de versión.

### 4.5. Operadores lógicos

Las constantes y los predicados de versión se evalúan como valores booleanos, que se pueden combinar con el conjunto estándar de operadores lógicos:

| Operador | Nombre | Ejemplo |
|----------|--------|---------|
| `!` | Negación | `!fabric`, `!=1.20` |
| `\|\|` | Unión (OR) | `fabric \|\| forge`, `1.20 \|\| 1.21` |
| `&&` | Intersección (AND) | `1.20 && fabric` |
| `( )` | Agrupación | `fabric && (1.20 \|\| 1.21)` |

### 4.6. Cadenas de condiciones (elif / else)

Las condiciones pueden ramificarse con `elif` (o `else if`) y `else` para proporcionar código distinto según el rango de versión. Las ramas se evalúan en orden y se habilita la **primera que coincida**:

```java
public static void example() {
    //? if 1.20.1 {
    System.out.println("Trails and Tales update just released!");
    //?} elif 1.21.1 {
    /*System.out.println("Tricky Trials update just released!");
    *///?} else if 1.21.4 {
    /*System.out.println("The Garden Awakens .. drop .. just dropped...");
    *///?}
}
```

### 4.7. Anidamiento de condiciones

Los bloques condicionales se pueden **anidar** unos dentro de otros. Para evitar problemas con los comentarios multilínea, `/* */` se sustituye por `/^ ^/`, que además puede incluir números en superíndice para gestionar correctamente los bloques.

```java
//? if <1.21 {
method1();
    //? if <1.20 {
    method2();
        //? if <1.19 {
        method3();
        //?}
    //?}
//?}
```

---

## 5. Swaps

Al escribir código versionado, a menudo necesitas modificar el mismo fragmento en muchos sitios, lo que añade mucho *boilerplate*.

Un **swap** es un **valor definido condicionalmente en el build script** que se **inserta en el archivo fuente** cuando este se procesa.

### 5.1. Sintaxis de la sentencia

Un swap empieza con el marcador `$`, seguido del nombre del swap (y, opcionalmente, parámetros):

```
$ {id del swap} (parámetros)
```

```java
//$ my_swap
method1();
```

> 💡 **Convención:** los swaps se nombran en `snake_case`.

### 5.2. Declaración de swaps

```kotlin
// build.gradle.kts
stonecutter {
    swaps["my_swap"] = when {
        eval(current.version, "<1.21") -> "method1();"
        else -> "method2();"
    }
}
```

En la versión `1.20.1` el `//$ my_swap` se sustituye por `method1();`, y en `1.21.1` por `method2();`.

### 5.3. Swaps parametrizados

Los parámetros de un swap rellenan plantillas predefinidas caso por caso. Los argumentos se listan **después del id del swap** y se insertan en la cadena sustituida:

```java
void main() {
    //$ my_swap argument '"long quoted string"'
    method1(argument, "long quoted string")
}
```

La plantilla se declara en el build script con **marcadores posicionales** (`$1`, `$2`, ...):

```kotlin
// build.gradle.kts
stonecutter {
    swaps["my_swap"] = when {
        current.parsed >= "1.21" -> "method1($1, $2);"
        else -> "method2($2, $1);"
    }
}
```

En `1.21`, el resultado sería `method2("long quoted string", argument)` (los argumentos se invierten).

### 5.4. Swaps locales

Para segmentos pequeños, los swaps se pueden definir **directamente en el texto**:

```java
void example() {
    Clazz
        //$ if >=1.21 '.method1()' else '.method2()'
        .method1()
        .method3();
}
```

En `1.21`, la línea se convierte en `.method2()`.

También puedes incluir cláusulas `elif` / `else if` en medio de la condición:

```
//$ if >=1.21 '.method1()' elif >=1.20 'method2()' else if >=1.20 'method3()' else '...'
```

> ⚠️ Los swaps locales pueden complicarse mucho; para casos complejos se recomienda registrarlos en el build script.

---

## 6. Replacements

Un **replacement** busca el patrón proporcionado en **todo el archivo** y sustituye las coincidencias por nuevos valores.

Los replacements son quizá la característica de sintaxis más versátil, ya que pueden modificar archivos con poco o ningún *boilerplate*. Sin embargo, si se usan mal, pueden modificar archivos de formas inesperadas.

### 6.1. Replacements globales

Se definen en el build script y, por defecto, se aplican a **todos los archivos** procesados con Stonecutter. Pueden recibir un **identificador** para poder activarse/desactivarse a lo largo del archivo.

### 6.2. Replacements de cadena (String)

Son los más eficientes y fiables. Usan la búsqueda **Aho-Corasick**: todas las coincidencias se encuentran en una sola pasada sobre el texto. Además, se implementan varias medidas de seguridad para evitar la corrupción del archivo por valores de reemplazo inválidos.

El parámetro que se pasa a la función `string` se llama **dirección** (*direction*) y hace que el reemplazo sea fácilmente reversible. En este ejemplo, cuando la dirección es `true`, `io.github.me` se sustituye por `dev.me`, y viceversa cuando es `false`.

```kotlin
// build.gradle.kts
stonecutter {
    replacements.string(current.parsed >= "1.21") {
        replace("io.github.me", "dev.me")
        // ...reemplazos adicionales
    }
}
```

### 6.3. Replacements con regex

Pueden capturar patrones más complejos en el código, con soporte para **sustitución por grupos** y **anclas de lookaround**.

Los grupos de reemplazo se capturan con `()` y se sustituyen en el mismo orden usando `$n`.

> ⚠️ A diferencia de los replacements de cadena, `new$1Context` **no** es un patrón válido para convertirse de vuelta en `old$1Context`. Por lo tanto, en los replacements con regex **debes especificar tú mismo la operación inversa**. Es tu responsabilidad asegurarte de que ambos reemplazos sean válidos.

```kotlin
// build.gradle.kts
stonecutter {
    replacements.regex(current.parsed >= "1.21") {
        replace("old(\\w+)Context" to "new$1Context", "new(\\w+)Context" to "old$1Context")
        // ...reemplazos adicionales
    }
}
```

### 6.4. Identificadores de reemplazo

Por defecto, los replacements afectan a todos los archivos, pero no siempre es lo deseado. Para solucionarlo, los replacements pueden recibir **identificadores**, lo que los **desactiva por defecto**.

Los identificadores se declaran como parámetro opcional durante el registro del reemplazo:

```kotlin
stonecutter {
    // El mismo parámetro existe para los replacements con regex
    replacements.string(current.parsed >= "1.21", "me_imports") {
        replace("io.github.me", "dev.me")
    }
}
```

Alternativamente, los identificadores se pueden declarar con el prefijo `!` para **activarlos por defecto**:

```kotlin
stonecutter {
    replacements.string(current.parsed >= "1.21", "!me_imports") {
        replace("io.github.me", "dev.me")
    }
}
```

> 💡 **Convención:** los identificadores de reemplazo se nombran en `snake_case`.

#### Activar/desactivar replacements en el archivo

A lo largo del archivo, los replacements se pueden **desactivar** usando el prefijo `!`. El conmutador actúa como un **latch SR**: usar el operador `!` varias veces sobre un reemplazo lo mantendrá desactivado.

```java
import io.github.me.Cls1;
//~ !me_imports   // desactiva el reemplazo
import io.github.me.Cls2;
//~ me_imports    // activa el reemplazo
import io.github.me.Cls3;
```

En `1.21`, `Cls1` y `Cls3` quedan como `dev.me.*` (reemplazo activo), mientras que `Cls2` permanece como `io.github.me.*` (reemplazo desactivado).

Se pueden conmutar **varios identificadores en el mismo comentario**:

```
//~ enable_me !disable_me !disable_me_too
```

### 6.5. Replacements locales

Para cambios pequeños, los replacements se pueden definir **en el propio archivo**, afectando al alcance seleccionado:

```java
//~ if >=1.21 '.getName()' -> '.name' {
var instance1 = object1.getName();
var instance2 = object2.getName();
//~}
```

En `1.21`, las llamadas `getName()` dentro del bloque se convierten en `.name`.

También se pueden **anidar ámbitos de reemplazo local**, que se **acumulan**:

```java
//~ if >=1.21 '.getName()' -> '.name' {
var instance1 = object1.getName();
//~ if >=1.21 '.getValue()' -> '.value'
var instance2 = object2.getName().getValue();
//~}
```

En `1.21`, `instance1` pasa a `object1.name` y `instance2` a `object2.name.value`.

> 💡 Los valores de reemplazo local pueden escribirse **sin comillas** cuando son identificadores válidos (o, por simplicidad, nombres de clase/método/campo de Java). En otros casos solo se aceptan **comillas simples**, y puedes escapar las de dentro con `\'`.

#### Replacements locales con nombre

En lugar de un alcance, un reemplazo local puede recibir un **identificador**, lo que hace que se aplique **hasta el final del archivo** (hasta que se desactive). Varios reemplazos pueden compartir el mismo identificador y, si lo comparten con un reemplazo global, este también se activará.

```java
//~ if >=1.21 '.getName()' -> '.name' as n
var instance1 = object1.getName();
var instance2 = object2.getName();
//~ !name_getter
var instance3 = object3.getName();
```

Usa `as _` para declarar un reemplazo **anónimo** cuando no necesites conmutarlo:

```
~ if >=1.21 '.getName()' -> '.name' as _
```

> ⚠️ **Disponibilidad:** en `0.9` y versiones anteriores (como la de este proyecto, `0.9.8`), los replacements locales con nombre y anónimos **no están disponibles**.

### 6.6. Mecánica de reemplazo

El motor de reemplazo implementa un conjunto de mecánicas para garantizar la estabilidad de los reemplazos (en la mayoría de los casos). Los rangos de coincidencia se almacenan en una cola antes de aplicarse.

#### Solapamiento de reemplazos (Replacement Overlapping)

Cuando una nueva coincidencia se solapa con una existente, ambas no pueden coexistir (podría corromper el archivo). Para resolverlo se aplican estas reglas **en orden**:

1. **Prioridad de coincidencia anterior (Earlier Match Priority):** cuando las coincidencias tienen distinta posición de inicio, gana la que aparece **antes** en el archivo.
2. **Prioridad de coincidencia más larga (Longer Match Priority):** cuando empiezan en la misma posición, gana la **más larga**.
3. **Prioridad de coincidencia más nueva (Newer Match Priority):** si dos reemplazos coinciden exactamente con el mismo segmento, se aplica el **último reemplazo registrado**. Esto solo puede ocurrir con regex, cuyos patrones pueden coincidir con segmentos de tamaño dinámico.

#### Preservación de reemplazos (Replacement Preservation)

Tanto la entrada como la salida de los reemplazos de cadena se buscan en el texto:

```java
//~ if true 'MyClass' -> 'MyClassRewrite'
class MyClassRewrite {
}
```

Aquí se encuentran tanto `MyClass` como `MyClassRewrite`, pero este último tiene **precedencia**: el valor se considera **ya reemplazado**, de modo que no termina como `MyClassRewriteRewrite`.

---

## 7. Cómo se usa en este proyecto (LootBeanStonecutter)

Este proyecto es la plantilla *Stonecutter Mod Template* (Java-only), adaptada a **LootBeamsContinued**. Usa Stonecutter `0.9.8` para mantener una única base de código para **Fabric**, **NeoForge** y **Forge**, en varias versiones de Minecraft.

### 7.1. Archivos clave

| Archivo | Función |
|---------|---------|
| `../settings.gradle.kts` | Declara el plugin de Stonecutter y crea las versiones/proyectos. |
| `../stonecutter.gradle.kts` | Script raíz: activa la versión y define `constants`, `swaps` y `targets` globales. |
| `../stonecutter.properties.toml` | Metadatos del mod y dependencias por versión/cargador. |
| `.sc_active_version` | Versión activa actual (la que usa el IDE y los comandos de Gradle). |
| `../versions` | Cada versión generada tiene su propio directorio (`26.3-fabric`, `1.21.1-neoforge`, etc.). |
| `../build.fabric.gradle.kts`, `build.neoforge.gradle.kts`, `build.forge.gradle.kts` | Build scripts específicos de cada cargador. |

### 7.2. Versiones y cargadores

En `../settings.gradle.kts`, la función `match(version, *loaders)` crea una entrada `versión-cargador` por cada cargador:

```kotlin
stonecutter {
    create(rootProject) {
        fun match(version: String, vararg loaders: String) =
            loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

        match("26.3", "fabric", "neoforge")
        match("26.2", "fabric", "neoforge")
        match("26.1.2", "fabric", "neoforge")
        match("1.21.11", "fabric", "neoforge")
        match("1.21.1", "fabric", "neoforge")
        //match("1.19.2", "fabric", "forge")

        vcsVersion = "26.3-fabric"
    }
}
```

### 7.3. Parámetros globales (`../stonecutter.gradle.kts`)

El bloque `stonecutter parameters { ... }` define, para todo el proyecto, las constantes y swaps usados en los comentarios:

```kotlin
stonecutter active file(".sc_active_version")

stonecutter parameters {
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["mod_id"] = "\"${properties.get<String>("mod.id")}\";"
    swaps["mod_name"] = "\"${properties.get<String>("mod.name")}\";"
    swaps["mod_group"] = "\"${properties.get<String>("mod.group")}\";"
    swaps["minecraft"] = "\"${current.version}\";"
    constants["release"] = properties.get<String>("mod.id") != "modtemplate"
}
```

- `constants.match(...)`: crea las constantes `fabric`, `neoforge` y `forge` (verdadera solo para el cargador activo), extraído de la parte final del nombre del proyecto (`26.3-fabric` → `fabric`).
- `swaps["mod_version"]`, `swaps["mod_id"]`, etc.: insertan los metadatos del mod (de `../stonecutter.properties.toml`) directamente en el código.
- `swaps["minecraft"]`: inserta la versión activa del juego como cadena.
- `constants["release"]`: `true` cuando el mod ya no es la plantilla (`mod.id != "modtemplate"`).

### 7.4. Metadatos del mod (`../stonecutter.properties.toml`)

```toml
[mod]
id = "lootbeams"
name = "LootBeamsContinued"
group = "com.github.razorplay"
version = "1.0.0"
channel_tag = ""
description = "Renders highly configurable colored beams on dropped items!"
authors = ["RazorPlay01"]
# ...
```

Las dependencias específicas por versión/cargador se definen en secciones como:

```toml
[fabric."1.21.1"]
deps.minecraft = "1.21.1"
deps.fabric-api = "0.116.7+1.21.1"
deps.modmenu = "11.0.4"

[neoforge."26.3"]
deps.minecraft = "26.3"
deps.neoforge = "26.3.0.7-beta"
```

### 7.5. Resumen práctico

- **Cambiar de versión activa:** modifica `.sc_active_version` (o usa el botón del plugin de IntelliJ).
- **Escribir código versionado:** usa `//? if ...` para condiciones, `//$ swap` para valores y `//~ ...` para replacements.
- **Configurar constantes/swaps globales:** en `../stonecutter.gradle.kts`.
- **Configurar versiones/cargadores:** en `../settings.gradle.kts`.
- **Configurar metadatos y dependencias:** en `../stonecutter.properties.toml`.

---

## Referencias

- Documentación oficial de Stonecutter: <https://stonecutter.kikugie.dev/wiki/>
- Repositorio de la plantilla: <https://github.com/rotgruengelb/stonecutter-mod-template>
- Comunidad (Discord de KikuGie): <https://discord.kikugie.dev/>






