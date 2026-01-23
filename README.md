# LO_LINKING_PARK

#PARKIMETRE

11,12,13,14 Explorar

Idear
 Com fer l'applicacio per grups.
 Solucio individual res programable.
 Esquema

Entrega 
 Indv 
 Grup

Materialitzar desde dimarts a Febrer 


OBJECTIUS
 ANDROID STUDIO

 Parkimetre de la Salle (opicions llistat de les Salles Catalunya)
 Opcions Admin Usuari
 Poder entrar dades coches
 1-5 Cotxes per user
 Dades persona i conte bancari 
 Iniciar parkimetre 
 Finalitzar (per temps o manual) -> paga proporcional a temps 
 Activar localització
 Que surti lloc on aparcar
 Marca un maxim (el tria el admin)

 Ens a de avisar en x temps (el tria el user)
 Quan finalitza sempre avisa 

 Historics
 NO RENOVACIONS





# CLASSES
# Parking:
    -ID:int(AI)
    -Ubi:(?)
    -Nom:varchar
    -Limit:int
    -Plaçes:int
    -TempsLimit:Date||int
-------------------------------
    +Is_Ple(Limit,Plaçes):bool

# Persona:
    -ID(DNI):varchar [00000000~99999999,+ A~Z]
    -Nom:varchar
    -Cognoms:varchar
--------------------------------
    +Verificar_DNI(Dni:int):bool

# Coche:
    -ID(MATRICULA):varchar [0000~9999,+AAA~ZZZ]
    -ID_Persona(FK Persona(ID))
    -Color:varchar
    -Marca:varchar
    ??? Model:varchar
--------------------------------

# Reserva:
    -ID:int(AI)
    -ID_Persona(FK Persona(ID))
    -ID_Parking(FK Parking(ID))
    -ID_Coche(FK Coche(ID))
    -Data_In:Date
    -Data_Out:Date
--------------------------------
     Finalitzar_Reserva(ID)
     Crear_Reserva()