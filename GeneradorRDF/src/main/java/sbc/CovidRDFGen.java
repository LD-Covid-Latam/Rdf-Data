package sbc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * @Title CSVGeneratorRDF
 * @Description CSVGen to create resource from a CSV file with peope information
 * @author Jean Paul Mosquera Arevalo
 */

public class CovidRDFGen {
    //CAMBIAR POR RUTAS DEL EQUIPO DONDE SE EJECUTE
    static String FolderPath="C:/Users/Marco/Desktop/DataValidToProcess/";
    static String govData="CovidGov.csv";
    static String countryStatsData="BingLatamPaises.csv";
    static String provinceStatsData="BingLatamProvince.csv";
    static String COpatientData="CovidPacientesCo.csv";
    static String ecStatsData="ecData.csv";
    static String testData="testcovid.csv";
    static String countryData="pais.csv";
    static String provinceData="provincias.csv";
    static String GenFilePath= "C:/Users/Marco/Desktop/DataValidToProcess/Covid-LATAM.rdf"; //Generated RDF

    static String uriContinent ="https://ld.utpl.edu.ec/dataCOVID/data/continent/";
    static String uriCountry="https://ld.utpl.edu.ec/dataCOVID/data/country/";
    static String uriProvince="https://ld.utpl.edu.ec/dataCOVID/data/province/";

    static String uriStatistic="https://ld.utpl.edu.ec/dataCOVID/data/statistic/";
    static String uriConfirmed="https://ld.utpl.edu.ec/dataCOVID/data/statistic/confirmed-cases/";
    static String uriRecovered="https://ld.utpl.edu.ec/dataCOVID/data/statistic/recovered-cases/";
    static String uriDeaths="https://ld.utpl.edu.ec/dataCOVID/data/statistic/deaths-cases/";
    static String uriActive="https://ld.utpl.edu.ec/dataCOVID/data/statistic/active-cases/";
    static String uriHospitalized="https://ld.utpl.edu.ec/dataCOVID/data/statistic/hospitalized-cases/";
    static String uriTests="https://ld.utpl.edu.ec/dataCOVID/data/tests/realized/";
    static String uriDataset="https://ld.utpl.edu.ec/dataCOVID/data/dataset/";
    static String uriPatient="https://ld.utpl.edu.ec/dataCOVID/data/patient/";
    static String uriMedicalInformation="https://ld.utpl.edu.ec/dataCOVID/data/medical-information/";
    static String uriContainmentMeasures="https://ld.utpl.edu.ec/dataCOVID/data/containment-measures/";
    static String uriCaseCovid="https://ld.utpl.edu.ec/dataCOVID/data/case-covid/";

    public static void main(String... args) throws FileNotFoundException {
        //Get data from CSV and store in a list
        List<Country> countries = readCountriesFromCSV(FolderPath+countryData);
        List<CountryCovidStats> statscountry = readStatsCFromCSV(FolderPath+countryStatsData);
        List<covidTests> tests = readTestsCFromCSV(FolderPath+testData);
        List<GovActions> actions = readActionsCFromCSV(FolderPath+govData);
        List<ProvinceCovidStats> statsprovince = readStatsPFromCSV(FolderPath+provinceStatsData);
        List<PatientData> patient = readPatientCFromCSV(FolderPath+COpatientData);
        List<Provinces> provinces = readProvincesFromCSV(FolderPath+provinceData);
        // create an empty Model
        Model model = ModelFactory.createDefaultModel();

        File f= new File (GenFilePath); //File to save the results of RDF Generation
        FileOutputStream os = new FileOutputStream(f);

        //Set prefix for the URI base (data)
        String dataPrefix = "https://ld.utpl.edu.ec/dataCOVID/data/";
        model.setNsPrefix("covidData",dataPrefix);


        String ontoPrefix="https://ld.utpl.edu.ec/dataCOVID/ontology#";
        Model ontoModel = ModelFactory.createDefaultModel();
        ontoModel.setNsPrefix("covidOnto",ontoPrefix);

        //Vocab and models present in JENA
        //SCHEMA
        String schema = "http://schema.org/";
        model.setNsPrefix("schema", schema);
        Model schemaModel = ModelFactory.createDefaultModel();
        //Dbpedia Ontology- DBO
        String dbo = "http://dbpedia.org/ontology/";
        model.setNsPrefix("dbo", dbo);
        Model dboModel = ModelFactory.createDefaultModel();
        //Dbpedia Resource - DBR
        String dbr = "http://dbpedia.org/resource/";
        model.setNsPrefix("dbr", dbr);
        Model dbrModel = ModelFactory.createDefaultModel();
        //Geonames - gn
        String gn = "http://www.geonames.org/ontology#";
        model.setNsPrefix("gn", gn);
        Model gnModel = ModelFactory.createDefaultModel();

        //DCAT
        String dcat = "http://www.w3.org/ns/dcat#";
        model.setNsPrefix("dcat", dcat);
        Model dcatModel = ModelFactory.createDefaultModel();

        //SIO
        String sio = "https://semanticscience.org/resource/SIO_000393/";
        model.setNsPrefix("sio", sio);
        Model sioModel = ModelFactory.createDefaultModel();

        //OV
        String ov = "http://open.vocab.org/terms/";
        model.setNsPrefix("ov", ov);
        Model ovModel = ModelFactory.createDefaultModel();

        //prov
        String prov = "http://www.w3.org/ns/prov#";
        model.setNsPrefix("prov", prov);
        Model provModel = ModelFactory.createDefaultModel();



        // let's print all the person read from CSV file
        /**
         * CountryCovidStats a : statscountry
         * Country a: country
         *
         * GovActions a : actions
         * covidTests a : tests
         * ProvinceCovidStats a: statsprovince
         * PatientData a : patient
         */
        /**
         private String continent;
         private String code;
         private String name;
         private String latitude;
         private String longitude;
         private int population;
         private float pib;
*/
        for(Country a : countries ){
            System.out.println(a);
            String nombre=a.getName().replaceAll(" ", "_");
            Resource rOc=model.createResource(uriCountry+a.getCode())
                    .addProperty(OWL.sameAs, dboModel.getProperty(dbr, nombre))
                    .addProperty(RDF.type, dboModel.getProperty(dbo, "Country"))
                    .addProperty(dboModel.getProperty(dbo,"name"), a.getName())
                    .addProperty(gnModel.getProperty(gn,"countryCode"), a.getCode())
                    .addProperty(schemaModel.getProperty(schema,"latitude"), a.getLatitude())
                    .addProperty(schemaModel.getProperty(schema,"longitude"), a.getLongitude())
                    .addProperty(dboModel.getProperty(dbo,"population"), String.valueOf(a.getPopulation()))
                    .addProperty(dboModel.getProperty(dbo,"grossDomesticProductNonimalPerCapita"), String.valueOf(a.getPib()))
                    .addProperty(model.getProperty(ontoPrefix, "geometry"), a.getGeometry());
        }
        for(CountryCovidStats a: statscountry ){
            System.out.println(a);
            Resource rOc=model.createResource(uriConfirmed+a.getCountryCode2()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getConfirmed()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriCountry+a.getCountryCode2()));

            Resource rOc1=model.createResource(uriRecovered+a.getCountryCode2()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getRecovered()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriCountry+a.getCountryCode2()));
            Resource rOc2=model.createResource(uriDeaths+a.getCountryCode2()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getDeath()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriCountry+a.getCountryCode2()));
        }
        for(Provinces a : provinces ){
            System.out.println(a);
            Resource rOc=model.createResource(uriProvince+a.getProvinceCode())
                    .addProperty(OWL.sameAs, dboModel.getProperty(dbr, a.getDbpedia()))
                    .addProperty(RDF.type, dboModel.getProperty(dbo, "Province"))
                    .addProperty(dboModel.getProperty(dbo,"province"), model.getProperty(uriCountry, a.getCountryCode()))
                    .addProperty(dboModel.getProperty(dbo,"name"), a.getProvinceName());
        }
        for(ProvinceCovidStats a: statsprovince ){
            System.out.println(a);
            Resource rOc=model.createResource(uriConfirmed+a.getProvinceCode()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getConfirmed()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriProvince+a.getProvinceCode()));

            Resource rOc1=model.createResource(uriRecovered+a.getProvinceCode()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getRecovered()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriProvince+a.getProvinceCode()));
            Resource rOc2=model.createResource(uriDeaths+a.getProvinceCode()+"/"+a.getRegID())
                    .addProperty(model.getProperty(ontoPrefix, "quantity"), String.valueOf(a.getDeath()))
                    .addProperty(schemaModel.getProperty(schema,"observationDate"), a.getDate())
                    .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriProvince+a.getProvinceCode()));
         }

            for(GovActions a10: actions){
                String date=a10.getFechaImplementacion().replaceAll("/", "-");
                String codeAction=a10.getCountryCode2()+"/"+date;
                Resource rOc5=model.createResource(uriContainmentMeasures+codeAction)
                        //.addProperty(RDF.type, dboModel.getProperty(ontoModel, "ContaimentMeasures"))
                        .addProperty(dboModel.getProperty(dbo,"name"), a10.getMedida())
                        .addProperty(dboModel.getProperty(dbo,"date"), a10.getFechaImplementacion())
                        .addProperty(dboModel.getProperty(dbo,"description"), a10.getCategoria())
                        .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriCountry,a10.getCountryCode2()));
            }
            //   .addProperty(dboModel.getProperty(dbo,"country"), dbrModel.getProperty(dbr,a.get));



            for(covidTests a20 : tests ){
                String date=a20.getDate().replaceAll("/", "-");
                String codeTest=a20.getCountryCode2()+"/"+date;
                Resource rOc20=model.createResource(uriTests+codeTest)
                        //.addProperty(RDF.type, dboModel.getProperty(ontoModel, "ContaimentMeasures"))
                        .addProperty(ontoModel.getProperty(ontoPrefix,"realized"), a20.getTotalTests())
                        .addProperty(schemaModel.getProperty(schema,"observationDate"), a20.getDate())
                        .addProperty(ovModel.getProperty(ov,"madeIn"), model.getProperty(uriCountry,a20.getCountryCode2()));
            }

            for(PatientData a40 : patient ){
                String codigo="CO/"+a40.getIdCase();
                if(a40.getFechamuerte()!=""){
                    Resource rOc60=model.createResource(uriMedicalInformation+codigo)
                            .addProperty(ontoModel.getProperty(ontoPrefix, "date_first_symptom"), a40.getDateNotification())
                            .addProperty(dboModel.getProperty(dbo,"deathDate"), a40.getFechamuerte())
                            .addProperty(dboModel.getProperty(dbo,"currentStatus"), a40.getEstado());
                }else{
                    Resource rOc60=model.createResource(uriMedicalInformation+codigo)
                            .addProperty(ontoModel.getProperty(ontoPrefix, "date_first_symptom"), a40.getDateNotification())
                            .addProperty(dboModel.getProperty(dbo,"currentStatus"), a40.getEstado());
                }
                Resource rPatient=model.createResource(uriPatient+codigo)
                        .addProperty(RDF.type,FOAF.Person)
                        .addProperty(FOAF.age, String.valueOf(a40.getEdad()))
                        .addProperty(FOAF.gender, a40.getGenero())
                        .addProperty(dboModel.getProperty(dbo,"place"), model.getProperty(uriCountry,"CO"))
                        .addProperty(ontoModel.getProperty(ontoPrefix,"hasData"), model.getProperty(uriMedicalInformation,codigo));

                Resource rCase=model.createResource(uriCaseCovid+codigo)
                        .addProperty(ontoModel.getProperty(ontoPrefix,"confirmationDate"), a40.getFechaDiagnostico())
                        .addProperty(ontoModel.getProperty(ontoPrefix,"hasData"), model.getProperty(uriPatient,codigo))
                        .addProperty(gnModel.getProperty(gn,"locatedIn"), model.getProperty(uriCountry,"CO"));


            }
/**
 * Reading the Generated data in Triples Format and RDF
 */
            StmtIterator iter = model.listStatements();
            System.out.println("TRIPLES");
            while (iter.hasNext()) {
                Statement stmt      = iter.nextStatement();  // get next statement
                Resource  subject   = stmt.getSubject();     // get the subject
                Property  predicate = stmt.getPredicate();   // get the predicate
                RDFNode   object    = stmt.getObject();      // get the object

                System.out.print(subject.toString());
                System.out.print(" " + predicate.toString() + " ");
                if (object instanceof Resource) {
                    System.out.print(object.toString());
                } else {
                    // object is a literal
                    System.out.print(" \"" + object.toString() + "\"");
                }

                System.out.println(" .");
            }
            // now write the model in XML form to a file
            System.out.println("MODELO RDF------");
            model.write(System.out, "RDF/XML-ABBREV");

            // Save to a file
            RDFWriter writer = model.getWriter("RDF/XML");
            writer.write(model,os, "");

            //Close models
            dboModel.close();
            model.close();

        }

    private static List<Country> readCountriesFromCSV(String fileName) {
        List<Country> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Country person = createCountry(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }

    private static List<Provinces> readProvincesFromCSV(String fileName) {
        List<Provinces> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                Provinces person = createProvince(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }

    private static List<CountryCovidStats> readStatsCFromCSV(String fileName) {
        List<CountryCovidStats> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                //System.out.println(attributes.length);
                CountryCovidStats person = createStatsCountry(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }

    private static List<ProvinceCovidStats> readStatsPFromCSV(String fileName) {
        List<ProvinceCovidStats> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");

                ProvinceCovidStats person1 = createStatsProvince(attributes);

                // adding person into ArrayList
                persons.add(person1);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }
    private static List<GovActions> readActionsCFromCSV(String fileName) {
        List<GovActions> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(";");

                GovActions person = createGovActions(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }

    private static List<PatientData> readPatientCFromCSV(String fileName) {
        List<PatientData> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(";");

                PatientData person = createPatientCo(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }

    private static List<covidTests> readTestsCFromCSV(String fileName) {
        List<covidTests> persons = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of
                // the file, using a comma as the delimiter
                String[] attributes = line.split(",");

                covidTests person = createTest(attributes);

                // adding person into ArrayList
                persons.add(person);

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return persons;
    }




    private static Country createCountry(String[] metadata) {
        String continent = metadata[0];
        String code = metadata[1];
        String name = metadata[2];
        String latitude = metadata[3];
        String longitude= metadata[4];
        int population = Integer.parseInt(metadata[5]);
        float pib = Float.parseFloat(metadata[6]);
        String geometry =  metadata[8];

        // create and return person of this metadata
        return new Country(continent,code,name,latitude,longitude,population,pib,geometry);
    }
    private static CountryCovidStats createStatsCountry(String[] metadata) {
        String regID = metadata[0];
        String date = metadata[1];
        int confirmed = Integer.parseInt(metadata[2]);
        int confirmedChange=Integer.parseInt(metadata[3]);
        int death = Integer.parseInt(metadata[4]);
        int deathChange = Integer.parseInt(metadata[5]);
        int recovered = Integer.parseInt(metadata[6]);
        int recoveredChange = Integer.parseInt(metadata[7]);
        String latitude = metadata[8];
        String longitude = metadata[9];
        String countryCode2 = metadata[10];
        String countryCode3 = metadata[11];
        String countryName = metadata[12];
        // create and return person of this metadata
        return new CountryCovidStats(regID,date,confirmed,confirmedChange,death,deathChange,recovered,recoveredChange,latitude,longitude,countryCode2,countryCode3,countryName);
    }
    private static ProvinceCovidStats createStatsProvince(String[] metadata) {
        String regID = metadata[0];
        String date = metadata[1];
        int confirmed = Integer.parseInt(metadata[2]);
        int confirmedChange=Integer.parseInt(metadata[3]);
        int death = Integer.parseInt(metadata[4]);
        int deathChange = Integer.parseInt(metadata[5]);
        int recovered = Integer.parseInt(metadata[6]);
        int recoveredChange = Integer.parseInt(metadata[7]);
        String latitude = metadata[8];
        String longitude = metadata[9];
        String countryCode2 = metadata[10];
        String countryCode3 = metadata[11];
        String countryName = metadata[12];
        String province= metadata[13];
        String provinceCode= metadata[14];
        // create and return person of this metadata
        return new ProvinceCovidStats(regID,date,confirmed,confirmedChange,death,deathChange,recovered,recoveredChange,latitude,longitude,countryCode2,countryCode3,countryName,province,provinceCode);
    }
    private static PatientData createPatientCo(String[] metadata) {
        String ageA= metadata[6].replaceAll("[^0-9]+", "");
        String idA=metadata[0].replaceAll("[^0-9]+", "");
        int idCase=Integer.parseInt(idA);

        String dateNotification=metadata[1];
        String divipCode=metadata[2];
        String ciudad=metadata[3];
        String departamento=metadata[4];
        String estado=metadata[5];
        int edad=Integer.parseInt(ageA);
        String genero=metadata[7];
        String tipo=metadata[8];
        String gravedad=metadata[9];
        String provenencia=metadata[10];
        String fechamuerte=metadata[12];
        String fechaDiagnostico=metadata[13];
        String fechaRecuperacion=metadata[14];
        String fechaReporte=metadata[15];
        String tipoRecuperacion=metadata[16];
        // create and return person of this metadata
        return new PatientData(idCase,dateNotification,divipCode,ciudad,departamento,estado,edad,genero,tipo,gravedad,provenencia,fechamuerte,fechaDiagnostico,fechaRecuperacion,fechaReporte,tipoRecuperacion);
    }
    private static GovActions createGovActions(String[] metadata) {
        String id=metadata[0];
        String countryName=metadata[1];
        String countryCode2=metadata[2];
        String countryCode3=metadata[3];
        String categoria=metadata[5];
        String medida=metadata[6];
        String comentarios=metadata[7];
        String fechaImplementacion=metadata[8];
        String fuente=metadata[9];
        String tipoFuente=metadata[10];
        String enlace=metadata[11];
        // create and return person of this metadata
        return new GovActions(id,countryName,countryCode2,countryCode3,categoria,medida,comentarios,fechaImplementacion,fuente,tipoFuente,enlace);
    }
    private static covidTests createTest(String[] metadata) {
        String countryCode3=metadata[0];
        String countryCode2=metadata[1];
        String continent=metadata[2];
        String location=metadata[3];
        String date=metadata[4];
        String totalTests=metadata[5];
        // create and return person of this metadata
        return new covidTests(countryCode3,countryCode2,continent,location,date,totalTests);
    }

    private static Provinces createProvince(String[] metadata) {
        String countryCode=metadata[0];
        String provinceCode=metadata[1];
        String provinceName=metadata[2];
        String dbpedia=metadata[3];

        // create and return person of this metadata
        return new Provinces(countryCode,provinceCode,provinceName,dbpedia);
    }
}

//Person Class
class Country {
    private String continent;
    private String code;
    private String name;
    private String latitude;
    private String longitude;
    private String geometry;
    private int population;
    private float pib;

    public Country(String continent, String code, String name, String latitude, String longitude, int population, float pib, String geometry) {
        this.continent = continent;
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.pib = pib;
        this.geometry = geometry;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public float getPib() {
        return pib;
    }

    public void setPib(float pib) {
        this.pib = pib;
    }

    public String getGeometry() {return geometry;}

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "Country{" + "continent=" + continent + ", code=" + code + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + ", population=" + population + ", pib=" + pib + '}';
    }



}
class CountryCovidStats{
    private String regID;
    private String date;
    private int confirmed;
    private int confirmedChange;
    private int death;
    private int deathChange;
    private int recovered;
    private int recoveredChange;
    private String latitude;
    private String longitude;
    private String countryCode2;
    private String countryCode3;
    private String countryName;

    public CountryCovidStats(String regID, String date, int confirmed, int confirmedChange, int death, int deathChange, int recovered, int recoveredChange, String latitude, String longitude, String countryCode2, String countryCode3, String countryName) {
        this.regID = regID;
        this.date = date;
        this.confirmed = confirmed;
        this.confirmedChange = confirmedChange;
        this.death = death;
        this.deathChange = deathChange;
        this.recovered = recovered;
        this.recoveredChange = recoveredChange;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode2 = countryCode2;
        this.countryCode3 = countryCode3;
        this.countryName = countryName;
    }

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getConfirmedChange() {
        return confirmedChange;
    }

    public void setConfirmedChange(int confirmedChange) {
        this.confirmedChange = confirmedChange;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getDeathChange() {
        return deathChange;
    }

    public void setDeathChange(int deathChange) {
        this.deathChange = deathChange;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getRecoveredChange() {
        return recoveredChange;
    }

    public void setRecoveredChange(int recoveredChange) {
        this.recoveredChange = recoveredChange;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode2() {
        return countryCode2;
    }

    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }

    public String getCountryCode3() {
        return countryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        this.countryCode3 = countryCode3;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "CountryCovidStats{" + "regID=" + regID + ", date=" + date + ", confirmed=" + confirmed + ", confirmedChange=" + confirmedChange + ", death=" + death + ", deathChange=" + deathChange + ", recovered=" + recovered + ", recoveredChange=" + recoveredChange + ", latitude=" + latitude + ", longitude=" + longitude + ", countryCode2=" + countryCode2 + ", countryCode3=" + countryCode3 + ", countryName=" + countryName + '}';
    }

}

class PatientData{
    private int idCase;
    private String dateNotification;
    private String divipCode;
    private String ciudad;
    private String departamento;
    private String estado;
    private int edad;
    private String genero;
    private String tipo;
    private String gravedad;
    private String provenencia;
    private String fechamuerte;
    private String fechaDiagnostico;
    private String fechaRecuperacion;
    private String fechaReporte;
    private String tipoRecuperacion;

    public PatientData(int idCase, String dateNotification, String divipCode, String ciudad, String departamento, String estado, int edad, String genero, String tipo, String gravedad, String provenencia, String fechamuerte, String fechaDiagnostico, String fechaRecuperacion, String fechaReporte, String tipoRecuperacion) {
        this.idCase = idCase;
        this.dateNotification = dateNotification;
        this.divipCode = divipCode;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.estado = estado;
        this.edad = edad;
        this.genero = genero;
        this.tipo = tipo;
        this.gravedad = gravedad;
        this.provenencia = provenencia;
        this.fechamuerte = fechamuerte;
        this.fechaDiagnostico = fechaDiagnostico;
        this.fechaRecuperacion = fechaRecuperacion;
        this.fechaReporte = fechaReporte;
        this.tipoRecuperacion = tipoRecuperacion;
    }

    public int getIdCase() {
        return idCase;
    }

    public void setIdCase(int idCase) {
        this.idCase = idCase;
    }

    public String getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(String dateNotification) {
        this.dateNotification = dateNotification;
    }

    public String getDivipCode() {
        return divipCode;
    }

    public void setDivipCode(String divipCode) {
        this.divipCode = divipCode;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public String getProvenencia() {
        return provenencia;
    }

    public void setProvenencia(String provenencia) {
        this.provenencia = provenencia;
    }

    public String getFechamuerte() {
        return fechamuerte;
    }

    public void setFechamuerte(String fechamuerte) {
        this.fechamuerte = fechamuerte;
    }

    public String getFechaDiagnostico() {
        return fechaDiagnostico;
    }

    public void setFechaDiagnostico(String fechaDiagnostico) {
        this.fechaDiagnostico = fechaDiagnostico;
    }

    public String getFechaRecuperacion() {
        return fechaRecuperacion;
    }

    public void setFechaRecuperacion(String fechaRecuperacion) {
        this.fechaRecuperacion = fechaRecuperacion;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(String fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getTipoRecuperacion() {
        return tipoRecuperacion;
    }

    public void setTipoRecuperacion(String tipoRecuperacion) {
        this.tipoRecuperacion = tipoRecuperacion;
    }

    @Override
    public String toString() {
        return "PatientData{" + "idCase=" + idCase + ", dateNotification=" + dateNotification + ", divipCode=" + divipCode + ", ciudad=" + ciudad + ", departamento=" + departamento + ", estado=" + estado + ", edad=" + edad + ", genero=" + genero + ", tipo=" + tipo + ", gravedad=" + gravedad + ", provenencia=" + provenencia + ", fechamuerte=" + fechamuerte + ", fechaDiagnostico=" + fechaDiagnostico + ", fechaRecuperacion=" + fechaRecuperacion + ", fechaReporte=" + fechaReporte + ", tipoRecuperacion=" + tipoRecuperacion + '}';
    }

}
class ProvinceCovidStats{
    private String regID;
    private String date;
    private int confirmed;
    private int confirmedChange;
    private int death;
    private int deathChange;
    private int recovered;
    private int recoverdChange;
    private String latitude;
    private String longitude;
    private String countryCode2;
    private String countryCode3;
    private String countryName;
    private String provinceName;
    private String provinceCode;

    public ProvinceCovidStats(String regID, String date, int confirmed, int confirmedChange, int death, int deathChange, int recovered, int recoverdChange, String latitude, String longitude, String countryCode2, String countryCode3, String countryName, String provinceName, String provinceCode) {
        this.regID = regID;
        this.date = date;
        this.confirmed = confirmed;
        this.confirmedChange = confirmedChange;
        this.death = death;
        this.deathChange = deathChange;
        this.recovered = recovered;
        this.recoverdChange = recoverdChange;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode2 = countryCode2;
        this.countryCode3 = countryCode3;
        this.countryName = countryName;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getConfirmedChange() {
        return confirmedChange;
    }

    public void setConfirmedChange(int confirmedChange) {
        this.confirmedChange = confirmedChange;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getDeathChange() {
        return deathChange;
    }

    public void setDeathChange(int deathChange) {
        this.deathChange = deathChange;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getRecoverdChange() {
        return recoverdChange;
    }

    public void setRecoverdChange(int recoverdChange) {
        this.recoverdChange = recoverdChange;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode2() {
        return countryCode2;
    }

    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }

    public String getCountryCode3() {
        return countryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        this.countryCode3 = countryCode3;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String toString() {
        return "ProvinceCovidStats{" + "regID=" + regID + ", date=" + date + ", confirmed=" + confirmed + ", confirmedChange=" + confirmedChange + ", death=" + death + ", deathChange=" + deathChange + ", recovered=" + recovered + ", recoverdChange=" + recoverdChange + ", latitude=" + latitude + ", longitude=" + longitude + ", countryCode2=" + countryCode2 + ", countryCode3=" + countryCode3 + ", countryName=" + countryName + ", provinceName=" + provinceName + ", provinceCode=" + provinceCode + '}';
    }


}

class GovActions{
    private String id;
    private String countryName;
    private String countryCode2;
    private String countryCode3;
    private String categoria;
    private String medida;
    private String comentarios;
    private String fechaImplementacion;
    private String fuente;
    private String tipoFuente;
    private String enlace;

    public GovActions(String id, String countryName, String countryCode2, String countryCode3, String categoria, String medida, String comentarios, String fechaImplementacion, String fuente, String tipoFuente, String enlace) {
        this.id = id;
        this.countryName = countryName;
        this.countryCode2 = countryCode2;
        this.countryCode3 = countryCode3;
        this.categoria = categoria;
        this.medida = medida;
        this.comentarios = comentarios;
        this.fechaImplementacion = fechaImplementacion;
        this.fuente = fuente;
        this.tipoFuente = tipoFuente;
        this.enlace = enlace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode2() {
        return countryCode2;
    }

    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }

    public String getCountryCode3() {
        return countryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        this.countryCode3 = countryCode3;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getFechaImplementacion() {
        return fechaImplementacion;
    }

    public void setFechaImplementacion(String fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getTipoFuente() {
        return tipoFuente;
    }

    public void setTipoFuente(String tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    @Override
    public String toString() {
        return "GovActions{" + "id=" + id + ", countryName=" + countryName + ", countryCode2=" + countryCode2 + ", countryCode3=" + countryCode3 + ", categoria=" + categoria + ", medida=" + medida + ", comentarios=" + comentarios + ", fechaImplementacion=" + fechaImplementacion + ", fuente=" + fuente + ", tipoFuente=" + tipoFuente + ", enlace=" + enlace + '}';
    }


}

class covidTests{
    private String countryCode3;
    private String countryCode2;
    private String continent;
    private String location;
    private String date;
    private String totalTests;

    public covidTests(String countryCode3, String countryCode2, String continent, String location, String date, String totalTests) {
        this.countryCode3 = countryCode3;
        this.countryCode2 = countryCode2;
        this.continent = continent;
        this.location = location;
        this.date = date;
        this.totalTests = totalTests;
    }

    public String getCountryCode3() {
        return countryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        this.countryCode3 = countryCode3;
    }

    public String getCountryCode2() {
        return countryCode2;
    }

    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(String totalTests) {
        this.totalTests = totalTests;
    }

    @Override
    public String toString() {
        return "covidTests{" + "countryCode3=" + countryCode3 + ", countryCode2=" + countryCode2 + ", continent=" + continent + ", location=" + location + ", date=" + date + ", totalTests=" + totalTests + '}';
    }
}

class Provinces{
    private String countryCode;
    private String provinceCode;
    private String provinceName;
    private String dbpedia;

    public Provinces(String countryCode, String provinceCode, String provinceName, String dbpedia) {
        this.countryCode = countryCode;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.dbpedia = dbpedia;
    }

    public String getCountryCode() { return countryCode; }

    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDbpedia() { return dbpedia; }

    public void setDbpedia(String dbpedia) {
        this.dbpedia = dbpedia;
    }

    @Override
    public String toString() {
        return "Provinces{"+ "countryCode=" + countryCode + ", provinceCode=" + provinceCode + ", provinceName=" + provinceName + '}';
    }



}
