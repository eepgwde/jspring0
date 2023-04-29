# weaves
# This script file uses m_ from
# https://sourceforge.net/p/ill/site/base/ci/master/tree/bin/m_
#
# It has a function f_tpush to delete temporary files on exit.

h_list () {
    cat >&2 <<EOF

    Copy files into the project src directories

    $prog [-n] [-x] [-l outputfile] [-f inputfile] ${FUNCNAME##h_} sdirs3|sdirs4|sdir5

    sdirs3 lists all the eligible directories that are down of \$TOP $TOP
    then sdirs4 prints the package names
    then sdirs5 prints the pairs

    then sdirs6 will copy the package-info.java file changing the package name to the directory.

    sdirs7 will list those directories.

EOF
}


d_list () {
    test $# -ge 1 || return 1
    local cmd="$1"
    shift

    # : ${d_dir:=$PWD}
    # : ${d_file:=$(m_ -d $PWD mr 'appium-*.log')}
    # : ${d_log:=$(echo $d_file | sed 's/appium/adb/g')}

    case $cmd in
	apk*)
	    : ${d_dir:=apk/Mein_o2}
	    : ${d_log:=./apk.lst}
	    
	    case $cmd in
		apk1)
		    find $d_dir -type f -name '*.xml' -exec grep -q -s 'android:id=' {} \; -print | tee ${cmd}.lst
		    ;;
	    esac

	;;
	
	sdir*)
	    ## Generates package-info.java files
	    
	    : ${d_dir:=$PWD}
	    : ${d_log:=$d_dir/sdirs.lst}
	    
	    case $cmd in
		sdirs7)
		    : ${d_service:='*'}
		    : ${d_service:="package-info.java"}
		    $nodo find $(cat $d_dir/sdirs3.lst | xargs) -maxdepth 1 -type f -name "${d_service}" -print | if [ -n "$nodo" ]
		    then
			cat
		    else
			sort -u
		    fi
		    ;;
		sdirs6)
		    local tfile=$(mktemp)
		    f_tpush $tfile
		    : ${d_file:=package-info.java}
		    
		    cat ${d_dir}/sdirs5.lst | while read i j
		    do
			cat $d_file | sed -e 's/com.baeldung.finalkeyword/'${j}'/g' -e 's/is about/'${j}' is about/g' | if [ -n "$nodo" ]
			then
			    cat
			else
			    cat > $tfile
			fi
			# package-info.java to 
			$nodo cp $tfile $i/${d_file}
		    done
		    ;;
		sdirs5)
		    parallel -a $d_dir/sdirs3.lst -a $d_dir/sdirs4.lst --link echo {1} {2} | tee ${cmd}.lst
		    ;;
		sdirs4)
		    $FUNCNAME sdirs3 | cut -d/ -f6- | sed -e 's,/,.,g' | tee ${cmd}.lst
		    ;;
		sdirs3)
		    : ${d_service:='/resources/'}
		    $FUNCNAME sdirs2 | egrep -v "$d_service" | tee ${cmd}.lst
		    ;;
		sdirs2)
		    $FUNCNAME sdirs1 | awk '$1 >= 6 { print $2 }'
		    ;;
		sdirs1)
		    cat $d_log | awk -F/ '{ print NF, $0 }'
		    ;;
		sdirs)
		    test -n "$TOP" || return 2
		    test -d "$TOP" || return 4

		    : ${d_file:=$(m_ -d $d_dir mr ${d_service:='*.log'} )}

		    find "$TOP"/src/ -type d > $d_log
		    ;;
	    esac
	    ;;
		
	log)
	    egrep 'Running ' $d_file | sed 's/^.* Running //g' | \
		sed -e s/^\'//g -e s/\'$//g -e 's/^.*\/adb /adb /g' | $FUNCNAME cmd > $d_log
	    ;;
	view)
	    echo $1 $d_dir $d_file
	    ;;
    esac
}

d_xml () {
    test $# -ge 1 || return 1
    local cmd="$1"
    shift

    : ${d_dir:=pages}
    : ${d_file:=$(m_ -d "$d_dir" mr 'w*')}

    case $cmd in
	attnames)
	    $nodo xmlstarlet tr ${cmd}.xsl  $d_file
	    ;;
	cmdline)
	    # "/hierarchy/@index" => all hierarchy 0
	    # "//@resource-id" 
	    $nodo xmlstarlet sel -T -t -v $* $d_file
	    ;;
	all)
	    test -n "$d_service" || return 4
	    test -f "$d_service" || return 2
	    
	    for d_file in $(cat $d_service | xargs)
	    do
		echo -- $d_file
		$FUNCNAME $d_service $*
	    done
	    ;;
    esac
    
}

h_applog () {
    cat >&2 <<EOF

    Process output of appium

    $prog [-n] [-x] [-l outputfile] [-f inputfile] ${FUNCNAME##h_} [log|session]

    defaults:

    Sets inputfile to most recent appium-*.log file
    Sets output to adb-*.log and files it with the adb commands.

    log

    Processes appium log file given by inputfile to produce the output log file
    Contains only the adb commands

    session-create

    Extracts the last session create command from the appium-*.log as a JSON and writes it
    to a file called session-create.json

EOF

}

d_json_pp () {
    # pretty print JSON
    $nodo python3 -m json.tool
}

d_applog () {
    test $# -ge 1 || return 1
    local cmd="$1"
    shift

    : ${d_dir:=$PWD}
    : ${d_file:=$(m_ -d $PWD mr 'appium-*.log')}
    : ${d_log:=$(echo $d_file | sed 's/appium/adb/g')}

    case $cmd in
	session-create)
	    awk 'BEGIN { rec0= 0 }
rec0 > 0 { $1=""; $2=""; $3=""; sess0=$0 ; rec0= 0; next }
$3 ~ /^\[HTTP\]/ && $4 ~ /-->/ && $5 ~ /POST/ && $6 ~ /\/wd\/hub\/session$/ {
 rec0=NR+1; next
}
END { sub(/^[ ]+/, "", sess0); print sess0 }
' $d_file | e_json_pp > ${cmd}.json
	    ;;

	session-id)
	    test $# -ge 1 || return 2
	    test -f "$1" || return 4
	    jq '.value.sessionId' < $1 | tr -d \" 
	    ;;

	session-*)
	    local tag=${cmd##session-}
	    awk -v tag="[${tag}]" 'BEGIN { print tag } $3 == tag { print }' $d_file 
	    ;;

	cmd)
	    sed -e 's/shell \([^'\'']\)\(.*\)/shell '\''\1\2\'\''/g' 
	    ;;
	log)
	    egrep 'Running ' $d_file | sed 's/^.* Running //g' | \
		sed -e s/^\'//g -e s/\'$//g -e 's/^.*\/adb /adb /g' | $FUNCNAME cmd > $d_log
	    ;;
	view)
	    echo $1 $d_dir $d_file
	    ;;
    esac
}

# <groupId>ai.centa</groupId>
# <artifactId>weaves0</artifactId>
# <version>0.0.1-SNAPSHOT</version>
# <scope>system</scope>
# <systemPath>weaves0/target/weaves0-0.0.1-SNAPSHOT.jar</systemPath>

d_install () {
  test $# -ge 1 || return 1
  local cmd="$1"
  shift

  : ${d_dir:=$(basename $(pwd))}
  : ${d_file:=$1}
  : ${d_log:=$1}
  : ${d_service:=ai.centa}

  case $cmd in
	  weaves0)
      $nodo mvn package
      test -f $d_file || return 2
      $nodo mvn install:install-file \
          -Dfile=$d_file \
          -DgroupId=${d_service} \
          -DartifactId=$d_dir \
          -Dversion=0.0.1-SNAPSHOT \
          -Dpackaging=jar \
          -DgeneratePom=true
      ;;
  esac
}
