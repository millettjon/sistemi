#
# Bash integration for Sistemi Moderni web application.
#

SISTEMI_HOME="$(dirname $(readlink -f $BASH_SOURCE))"

if [ "$UID" == "0" ] ; then
    SUDO=""
else
    SUDO="sudo"
fi

# Prefix to use for aliases.
PREFIX=sm

alias ${PREFIX}="cd $SISTEMI_HOME"
