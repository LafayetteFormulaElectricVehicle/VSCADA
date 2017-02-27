CLASSPATHS=".:DBHandler/sqlite-jdbc-3.16.1.jar:VSCADAComp/gson-2.2.2.jar"
ECLASSPATHS=".:../DBHandler/sqlite-jdbc-3.16.1.jar:../VSCADAComp/gson-2.2.2.jar"

DIRS=Configuration/*.java DBHandler/*.java VSCADAComp/*.java
SAVEDIR=exec

#File to run
F=DBTest

all: dir
	javac -cp $(CLASSPATHS) $(DIRS) -d $(SAVEDIR)

dir:
	@mkdir -p $(SAVEDIR)

run:
	@cd $(SAVEDIR); \
	java -cp $(ECLASSPATHS) $(F)

clean:
	rm -rf $(SAVEDIR)
