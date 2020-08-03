/*------------------------------------------------------------
Fichier: cpr.c

Nom:
Numero d'etudiant:

Description: Ce programme contient le code pour la creation
             d'un processus enfant et y attacher un tuyau.
	     L'enfant envoyera des messages par le tuyau
	     qui seront ensuite envoyes a la sortie standard.

Explication du processus zombie
(point 5 de "A completer" dans le devoir):

	(s.v.p. completez cette partie);

-------------------------------------------------------------*/
#include <stdio.h>
#include <sys/select.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>
#include <string.h> 
#include <fcntl.h>
#include <stdlib.h>

/* Prototype */
void creerEnfantEtLire(int );

/*-------------------------------------------------------------
Function: main
Arguments: 
	int ac	- nombre d'arguments de la commande
	char **av - tableau de pointeurs aux arguments de commande
Description:
	Extrait le nombre de processus a creer de la ligne de
	commande. Si une erreur a lieu, le processus termine.
	Appel creerEnfantEtLire pour creer un enfant, et lire
	les donnees de l'enfant.
-------------------------------------------------------------*/

int main(int ac, char **av)
{
    int numeroProcessus; 

    if(ac == 2)
    {
       if(sscanf(av[1],"%d",&numeroProcessus) == 1)
       {
           creerEnfantEtLire(numeroProcessus);
       }
       else fprintf(stderr,"Ne peut pas traduire argument\n");
    }
    else fprintf(stderr,"Arguments pas valide\n");
    return(0);
}


/*-------------------------------------------------------------
Function: creerEnfantEtLire
Arguments: 
	int prcNum - le numero de processus
Description:
	Cree l'enfant, en y passant prcNum-1. Utilise prcNum
	comme identificateur de ce processus. Aussi, lit les
	messages du bout de lecture du tuyau et l'envoie a 
	la sortie standard (df 1). Lorsqu'aucune donnee peut
	etre lue du tuyau, termine.
-------------------------------------------------------------*/

void creerEnfantEtLire(int prcNum)
{
	char *arg[3];
	int status = 0;
	int wpid;
	arg[0] = "./cpr";
	arg[2] = NULL;
	int pid = 0;
	int fd[2];
	int ret = pipe(fd);
	if(ret == -1){
		printf("pipe failed \n");
		exit(1);
	}
	
	printf("processus %d commence \n", prcNum);
	if(prcNum > 1){
		pid = fork();
		if(pid == 0){
			dup2(fd[1],1);
			char str[5];
			int tmp = prcNum-1;
			sprintf(str, "%d", tmp);
			arg[1] = str;
			execvp(arg[0], arg);
		}else{	
			char ans[5000];
			read(fd[0], ans, 5000);
			printf("%s",ans);		
		}
	}else{
		sleep(5);
	}
	printf("processus %d fini \n", prcNum);
	exit(0);
}
