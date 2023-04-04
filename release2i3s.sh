set -e
rm -rf target/*.jar target/dependency/*jar onlinegraph-jars/
mvn package
mvn dependency:copy-dependencies
mkdir onlinegraph-jars
cp target/*.jar target/dependency/*jar onlinegraph-jars/
tar czvf onlinegraph-jars.tgz onlinegraph-jars
rm -rf onlinegraph-jars dependency-reduced-pom.xml
scp onlinegraph-jars.tgz hogie@bastion.i3s.unice.fr:public_html/software/onlineGraph/