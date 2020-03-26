// Configuración Firebase
var firebaseConfig = {
  //apiKey
  authDomain: "sedesembd.firebaseapp.com",
  databaseURL: "https://sedesembd.firebaseio.com",
  projectId: "sedesembd",
  storageBucket: "sedesembd.appspot.com",
  messagingSenderId: "1004275798936",
  appId: "1:1004275798936:web:5738b90915a706029b11ed"
};
// inicilizar Firebase
firebase.initializeApp(firebaseConfig);

var refDB = firebase.database().ref().child('registros');
var letra = ''; //Necesaria para la obtención y acomodo de las fotos de cada CURP
const primera = 'A';
const segunda = 'B';
const tercera = 'C';

//--------------------------------------------- Conexion Storage -----------------------------------------------------
var storage = firebase.storage();
var storageRef = storage.ref();
//---------------------------------------------------- Fin de conexión -------------------------------------------------------

refDB.on('value', gotData, errData);

function gotData(data) {
  var scores = data.val();
  var keys = Object.keys(scores);
  for (var i = 0; i < keys.length; i++) {
    var k = keys[i];
    var Lnombres = scores[k].nombre; 				//Nombre
    var Lcurp = scores[k].curp_id;   				//CURP
    var LapePat = scores[k].apePat;  				//Apellido Paterno
    var LapeMat = scores[k].apeMat;  				//Apellido Materno
    var Lsexo = scores[k].sexo; 	 					//Sexo
    var LfechaNac = scores[k].fechaNac;			//fecha de Nacimiento
    var Lentidad = scores[k].entidad;				//Entidad
    var LcodReg = scores[k].region;					//Codigo region
    var Llatitud = scores[k].latitud;				//Latitud
    var Llongitud = scores[k].longitud; 		//Longitud
    var Laltitud = scores[k].latitud;				//Latitud
    var Lprecision = scores[k].precision; 	//precision

    var tBody = document.getElementById('dataTable').lastElementChild;
    var tr1 = document.createElement('tr');
    tBody.appendChild(tr1);
    var dato1 = document.createElement('td');
    dato1.innerText = Lcurp;
    llenaFotos(Lcurp); //función para obtener las fotos según la CURP
    tr1.appendChild(dato1);
    var dato2 = document.createElement('td');
    dato2.innerText = Lnombres;
    tr1.appendChild(dato2);
    var dato3 = document.createElement('td');
    dato3.innerText = LapePat;
    tr1.appendChild(dato3);
    var dato4 = document.createElement('td');
    dato4.innerText = LapeMat;
    tr1.appendChild(dato4);
    var dato5 = document.createElement('td');
    dato5.innerText = Lsexo;
    tr1.appendChild(dato5);
    var dato6 = document.createElement('td');
    dato6.innerText = LfechaNac;
    tr1.appendChild(dato6);
    var dato7 = document.createElement('td');
    dato7.innerText = Lentidad;
    tr1.appendChild(dato7);
    var dato8 = document.createElement('td');
    dato8.innerText = LcodReg;
    tr1.appendChild(dato8);

    if (typeof Llatitud === 'undefined') { //Si el dato no se encuentra en la base de datos muestra los guiones
      var dato9 = document.createElement('td');
      dato9.innerText = "---";
      tr1.appendChild(dato9);
    } else {
      var dato9 = document.createElement('td');
      dato9.innerText = Llatitud;
      tr1.appendChild(dato9);
    } if (typeof Llongitud === 'undefined') {
      var dato10 = document.createElement('td');
      dato10.innerText = "---";
      tr1.appendChild(dato10);
    } else {
      var dato10 = document.createElement('td');
      dato10.innerText = Llongitud;
      tr1.appendChild(dato10);
    } if (typeof Laltitud === 'undefined') {
      var dato11 = document.createElement('td');
      dato11.innerText = "---";
      tr1.appendChild(dato11);
    } else {
      var dato11 = document.createElement('td');
      dato11.innerText = Laltitud;
      tr1.appendChild(dato11);
    } if (typeof Lprecision === 'undefined') {
      var dato12 = document.createElement('td');
      dato12.innerText = "---";
      tr1.appendChild(dato12);
    } else {
      var dato12 = document.createElement('td');
      dato12.innerText = Lprecision;
      tr1.appendChild(dato12);
    }
    for (var j = 0; j < 3; j++) {
      if (j == 0) { letra = 'A'; } //Se asigna la letra que identifica la foto en cuestión
      else if (j == 1) { letra = 'B'; }
      else if (j == 2) { letra = 'C'; }

      var y = document.createElement('td');
      var x = document.createElement("img");
      x.setAttribute("id", Lcurp + letra); //Se indica el identificador que tendrá para las imágenes
      x.setAttribute("width", "304");
      x.setAttribute("height", "228");
      x.setAttribute("alt", Lcurp + letra); //Se coloca un texto alternativo en caso que falle la carga de la imagen
      y.appendChild(x);
      tr1.appendChild(y);
    }
  };
}

function llenaFotos(curp) {
  for (var i = 0; i < 3; i++) {
    if (i == 0) { var posicion = primera; } //Se asigna la letra de cada foto presente en la Base de Datos
    else if (i == 1) { var posicion = segunda; }
    else if (i == 2) { var posicion = tercera; }

    colocarImagen(curp, posicion);
  }
}

function errData(err) {
  console.log('Error!');
  console.log(err);
}

function colocarImagen(curp, posicion) {
  storageRef.child(curp + posicion).getDownloadURL().then(function (url) { //Obtiene la url de la foto con el identificador correspondiente
    var img = document.getElementById(curp + posicion); //Obtiene el lugar, mediante DOM, donde se colocará la imagen correspondiente
    img.setAttribute("src", url); //Se agrega la imagen
  }).catch(function (error) {
    // Handle any errors
    console.log(curp + " ---"); //En caso que la curp no cuente con fotografías, se manda mensaje a consola
  });
}

function descargarExcel() {
  //Creamos un Elemento Temporal en forma de enlace
  var tmpElemento = document.createElement('a');
  // obtenemos la información desde el div que lo contiene en el html
  // Obtenemos la información de la tabla
  var data_type = 'data:application/vnd.ms-excel';
  var tabla_div = document.getElementById('dataTable');
  var tabla_html = tabla_div.outerHTML.replace(/ /g, '%20');
  tmpElemento.href = data_type + ', ' + tabla_html;
  var nombreExcel = document.getElementById('nameDownload').value;
  //Asignamos el nombre a nuestro EXCEL
  tmpElemento.download = nombreExcel + '.xls';
  // Simulamos el click al elemento creado para descargarlo
  tmpElemento.click();
}

var tableToExcel = (function () {
  var uri = 'data:application/vnd.ms-excel;base64,'
    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>'
    , base64 = function (s) { return window.btoa(unescape(encodeURIComponent(s))) }
    , format = function (s, c) { return s.replace(/{(\w+)}/g, function (m, p) { return c[p]; }) }
  return function (table, name) {
    if (!table.nodeType) table = document.getElementById(table)
    var ctx = { worksheet: name || 'Worksheet', table: table.innerHTML }
    window.location.href = uri + base64(format(template, ctx))
  }
})()

function downloadCSV(csv, filename) {
  var csvFile;
  var downloadLink;

  // Archivo CSV
  csvFile = new Blob([csv], { type: "text/csv" });

  // Link de descarga
  downloadLink = document.createElement("a");

  // Nombre de archivo
  downloadLink.download = filename;

  // Creación de link al archivo
  downloadLink.href = window.URL.createObjectURL(csvFile);

  // Ocultar link de descarga
  downloadLink.style.display = "none";

  // Agregar link al DOM
  document.body.appendChild(downloadLink);

  // Click a link de descarga
  downloadLink.click();
}

function exportTableToCSV(filename) {
  var csv = [];
  var rows = document.querySelectorAll("table tr");

  for (var i = 0; i < rows.length; i++) {
    var row = [], cols = rows[i].querySelectorAll("td, th");

    for (var j = 0; j < cols.length; j++) {
      if (cols[j].innerText != '') { //Detecta si contiene texto el campo
        row.push(cols[j].innerText);
      } else { //En caso negativo, obtiene la url de la imagen
        row.push(cols[j].querySelector('img').getAttribute('src'));
      }
    }
    csv.push(row.join(","));
  }

  // Descargar archivo CSV
  downloadCSV(csv.join("\n"), filename);
}
