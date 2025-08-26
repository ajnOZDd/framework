#!/bin/bash
# jar-deps.sh - Gestionnaire dynamique de dépendances JAR

# Config
D="${PROJECT_DIR:-$(pwd)}"
export PATH="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}/bin:$PATH"
SRC="$D/src" BIN="$D/bin" LIB="$D/lib" CFG="$D/.jar-deps" MF="$D/MANIFEST.MF"

# Utils
log() { echo "[$1] $2" ${3:+>&2}; }
init() { [ -f "$CFG" ] || { log I "Créé $CFG"; echo -e "# Chemins JAR (un par ligne)\n# /usr/share/tomcat/lib" > "$CFG"; }; }

# Gestion chemins
path() {
    [ -z "$2" ] && { log E "Usage: $0 $1 <chemin>" e; exit 1; }
    p=$(realpath "$2" 2>/dev/null || echo "$2")
    if [ "$1" = "add" ]; then
        [ ! -d "$p" ] && { log E "Inexistant: $p" e; exit 1; }
        init; grep -Fxq "$p" "$CFG" 2>/dev/null && { log W "Existe: $p" e; return; }
        echo "$p" >> "$CFG"; log I "Ajouté: $p ($(find "$p" -name "*.jar" 2>/dev/null|wc -l) JARs)"
    else
        [ -f "$CFG" ] && grep -Fxq "$p" "$CFG" && { grep -Fxv "$p" "$CFG" > "$CFG.tmp" && mv "$CFG.tmp" "$CFG"; log I "Supprimé: $p"; } || log W "Non trouvé: $p" e
    fi
}

# Scanner
scan() {
    [ ! -f "$CFG" ] && { log W "Pas de config" e; return; }
    log I "Scan JAR..."; echo "==============="; t=0
    while read -r p; do [[ "$p" =~ ^[[:space:]]*# ]] || [[ -z "$p" ]] && continue
        [ ! -d "$p" ] && { echo "⚠ $p"; continue; }; echo "📁 $p"; c=0
        find "$p" -name "*.jar" -type f 2>/dev/null | sort | while read -r j; do c=$((c+1))
            [ "$1" = "-v" ] && echo "  └── $(basename "$j") ($(du -h "$j" 2>/dev/null|cut -f1))"
        done; echo "   └── $(find "$p" -name "*.jar" 2>/dev/null|wc -l) JAR(s)"; t=$((t+$(find "$p" -name "*.jar" 2>/dev/null|wc -l)))
    done < "$CFG"; echo; echo "Total: $t JARs"
}

# Build classpath
cp() {
    CLASSPATH="$BIN"; [ -d "$LIB" ] && for j in "$LIB"/*.jar; do [ -f "$j" ] && CLASSPATH="$CLASSPATH:$j"; done 2>/dev/null
    [ -f "$CFG" ] && while read -r p; do [[ "$p" =~ ^[[:space:]]*# ]] || [[ -z "$p" ]] && continue
        [ -d "$p" ] && find "$p" -name "*.jar" -type f 2>/dev/null | while read -r j; do CLASSPATH="$CLASSPATH:$j"; done
    done < "$CFG"; export CLASSPATH
}

# Compile
comp() {
    log I "Compilation..."; [ ! -d "$SRC" ] && { log E "Pas de src/" e; exit 1; }
    mkdir -p "$BIN"; cp; f=$(find "$SRC" -name "*.java" -type f)
    [ -z "$f" ] && { log I "Pas de .java"; return; }
    log I "$(echo "$CLASSPATH"|tr ':' '\n'|grep -c '\.jar$' 2>/dev/null||echo 0) JARs"
    javac -cp "$CLASSPATH" -d "$BIN" -sourcepath "$SRC" $f && log I "✓ OK" || { log E "✗ Erreur" e; exit 1; }
}

# Run
run() { [ -z "$1" ] && { log E "Usage: $0 run <Classe>" e; exit 1; }; c="$1"; shift; cp; log I "Run $c..."; java -cp "$CLASSPATH" "$c" "$@"; }

# Find main
main() {
    [ ! -d "$BIN" ] && { log W "Pas de bin/" e; return 1; }; log I "Cherche main()..."; m=()
    find "$BIN" -name "*.class" -type f | while read -r f; do
        c=$(echo "$f"|sed "s|$BIN/||g"|sed 's|/|.|g'|sed 's|.class$||g')
        javap -cp "$BIN" "$c" 2>/dev/null|grep -q "public static void main" && m+=("$c")
    done 2>/dev/null
    case ${#m[@]} in 0) return 1;; 1) echo "${m[0]}";; *) log I "Plusieurs main:"; for i in "${!m[@]}"; do echo " $((i+1)). ${m[$i]}"; done
        read -p "Choix [1-${#m[@]}]: " x; [[ "$x" =~ ^[0-9]+$ ]] && [ "$x" -ge 1 ] && [ "$x" -le ${#m[@]} ] && echo "${m[$((x-1))]}" || return 1;; esac
}

# JAR avec destination personnalisée
# Créer JAR
# Créer JAR
jar() {
    local name="$1"; shift
    local mainClass="" dest="$D" noMain=0

    # Parsing des options
    while [ $# -gt 0 ]; do
        case "$1" in
            --no-main) noMain=1 ;;
            -o|--output) shift; dest="$1" ;;
            *) [ -z "$mainClass" ] && mainClass="$1" ;;
        esac
        shift
    done

    mkdir -p "$dest"
    cp

    # Crée MANIFEST
    if [ $noMain -eq 1 ]; then
        echo "Manifest-Version: 1.0" > "$MF"
    else
        if [ -z "$mainClass" ]; then
            mainClass=$(main) || { log E "Pas de main trouvé" e; exit 1; }
        fi
        echo -e "Manifest-Version: 1.0\nMain-Class: $mainClass" > "$MF"
    fi

    log I "Création du JAR $dest/$name ..."
    (cd "$BIN" && command jar cfm "$dest/$name" "$MF" .)
    log I "✓ JAR généré: $dest/$name"
}


# VS Code
vsc() {
    mkdir -p "$D/.vscode"; log I "Config VS Code..."
    { echo -e '{\n  "java.project.sourcePaths":["src"],\n  "java.project.outputPath":"bin",\n  "java.project.referencedLibraries":[\n    "lib/**/*.jar"'
      [ -f "$CFG" ] && while read -r p; do [[ "$p" =~ ^[[:space:]]*# ]] || [[ -z "$p" ]] && continue; [ -d "$p" ] && echo ",    \"$p/**/*.jar\""; done < "$CFG"
      echo -e '  ]\n}'; } > "$D/.vscode/settings.json"; log I "✓ Généré"
}

# Help
help() { cat << 'EOF'
jar-deps.sh - Gestionnaire JAR

Commands:
  add <path>                    Ajouter chemin
  rm <path>                     Supprimer chemin  
  scan [-v]                     Scanner JARs
  compile                       Compiler
  run <Class>                   Exécuter
  jar <nom> [main] [-o <dest>]  Créer JAR fat
  cp                            Voir classpath
  clean                         Nettoyer
  vscode                        Config VS Code

Options JAR:
  --no-main                     JAR sans classe main
  -o, --output <chemin>         Répertoire de destination

Exemples:
  ./jar-deps.sh add /usr/share/java
  ./jar-deps.sh jar app.jar
  ./jar-deps.sh jar app.jar Main -o /opt/apps
  ./jar-deps.sh jar lib.jar --no-main -o ~/libs
  ./jar-deps.sh run Main
EOF
}

# Main
case "${1:-help}" in
add|rm) path "$1" "$2";;
scan) scan "$2";;
compile) comp;;
run) shift; run "$@";;
jar) shift; jar "$@";;
cp) cp; echo "$CLASSPATH"|tr ':' '\n'|nl;;
clean) rm -rf "$BIN" "$D"/*.jar "$MF" 2>/dev/null; log I "Nettoyé";;
vscode) vsc;;
*) help;;
esac