# Makefile for bittorrent project

JAVAC = javac
FLAGS = -nowarn -g
JAVA_FILES = $(wildcard core/*.java) $(wildcard message/*.java) $(wildcard metafile/*.java) $(wildcard tests/*.java) $(wildcard tracker/*.java) $(wildcard utils/*.java)
CLASSPATH = .:lib/json-20160212.jar:lib/junit-4.12.jar
# JAVA_FILES = $(wildcard tracker/*.java)

.PHONY = all clean

all: $(JAVA_FILES)
	@echo 'Making all...'
	@$(JAVAC) -cp $(CLASSPATH) $(FLAGS) $?

clean:
	rm -f $(JAVA_FILES:.java=.class)
	rm -f *~ lib/*~ proj/*~
