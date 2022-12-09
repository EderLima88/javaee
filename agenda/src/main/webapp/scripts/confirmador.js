/**
 * 
 */
 
 function confirmar(idcon) {
	let resposta = confirm("Confirmar a exclusão")
	if(resposta === true) {
		//alert(idcon)
		window.location.href = "delete?idcon=" + idcon
	}
}