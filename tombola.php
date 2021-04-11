<?php
//ogni funzione stampa:
//	0 in caso di uscita senza errori
//	1 in caso di qualsiasi errore del file
// -1 in caso qualsiasi cosa esista già (file, etc) (uscita negativa)
error_reporting(E_ALL & ~E_NOTICE);
$func = $_GET['func'];
$nick = $_GET['nick'];
$file = "sessions/" . $nick . ".json";
switch($func) {
	case "newSession": // <---------------- tabellone - crea la sessione - newSession
    	if(is_dir("sessions") === false) {
        	if(mkdir("sessions") === false) {
            	echo("1");
                exit;
            }
		}
		if(file_exists($file)) { //se la sessione esiste
			echo("-1");
		} else { //se la sessione NON esiste
			$dati = [
				'numeri' => '0,',
				'nCaselle' => '1'
			];
			write(json_encode($dati));
			echo("0");
		}
		break;
	case "existSession": // <---------------- cartella - controlla se la sessione esiste - existSession
		if(file_exists($file)) {
			echo("0");
		} else {
			echo("-1");
		}
		break;
	case "delSession": // <---------------- tabellone - elimina la sessione - delSession
		if (unlink($file)) {
			echo("0");
		} else {
			echo("1");
		}
		break;
	case "setNumeri": // <---------------- tabellone setta i numeri - setNumeri
		$numeri = $_GET['numeri'];
		$dati = read();
		$dati['numeri'] = $numeri;
		write(json_encode($dati));
		echo("0");
		break;
	case "getNumeri": // <---------------- cartella - prende i numeri usciti - getNumeri
		$dati = read();
		echo($dati['numeri']);
		break;
	case "setNCartelle": // <---------------- tabellone - setta numero max di tabelle - setNCartelle
		$nCaselle = $_GET['n'];
		$dati = read();
		$dati['nCaselle'] = $nCaselle;
		write(json_encode($dati));
		echo("0");
		break;
	case "getNCartelle": // <---------------- cartella - prende il numero di cartelle - getNCartelle
		$dati = read();
		echo($dati['nCaselle']);
		break;
	case "addCartella": // <---------------- cartella - registra la cartella e indirizzo MAC (id) - addCartella
		//stampa 0 se è possibile aggiungere la casella, -1 se non è possibile aggiungere la casella
		$id = (string)$_GET['id'];
		$dati = read();
		if(array_key_exists($id, $dati)) {
			if($dati['nCaselle'] > $dati[$id]) {
				$dati[$id]++;
			}
			else if($dati['nCaselle'] == $dati[$id]) {
				echo("-1");
				exit;
			}
		} else {
			$dati[$id] = 1;
		}
		write(json_encode($dati));
		echo("0");
		break;
	case "delCartella": // <---------------- cartella - toglie la cartella una volta chiusa - delCartella
		$id = (string)$_GET['id'];
		$dati = read();
		if(array_key_exists($id, $dati)) {
			if($dati[$id] == 1) {
				unset($dati[$id]);
			}
			else if($dati[$id] > 1) {
				$dati[$id]--;
			}
		}
		write(json_encode($dati));
		echo("0");
		break;
	case "checkVer": // <---------------- tabellone/cartella - controlla la versione - controlloVersione
		//restituisce -1 se è presente una nuova versione, 0 se la versione è aggiornata
        if(file_exists("ver.txt") === false) {
        	writeSrt("ver.txt", "4.1");
        }
		$verServ = (float)readStr("ver.txt");
		$verProg = (float)$_GET['ver'];
		if($verProg < $verServ) { //esiste nuova versione
			echo("-1");
		} else { //non esiste
			echo("0");
		}
		break;
	default:
		echo("default");
}

function write($data) {
	if(file_put_contents($GLOBALS['file'], $data) === false) {
		echo("1");
		exit;
	}
}

function writeSrt($fileStr, $data) {
	if(file_put_contents($fileStr, $data) === false) {
		echo("1");
		exit;
	}
}

function read() {
	$fileData = file_get_contents($GLOBALS['file']);
	if($fileData === false) {
		echo("1");
		exit;
	}
	return (json_decode($fileData, true));
}

function readStr($fileStr) {
	$fileData = file_get_contents($fileStr);
	if($fileData === false) {
		echo("1");
		exit;
	}
	return (json_decode($fileData, true));
}
?>