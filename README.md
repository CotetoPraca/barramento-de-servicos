# Barramento de Serviços S3B

Implementação do barramento Small Scale Service Bus (SSSB ou S3B) criado como projeto 
de finalização de graduação em Ciência da Computação na Universidade Federal de Itajubá 
(UNIFEI).

O projeto foi testado com outras duas aplicações: um cliente Python e um servidor Java.
Os testes envolveram a troca de mensagens entre as aplicações usando os protocolos MQTT
e CoAP.

Esse projeto é dividido em 4 módulos:

- `inicializador`: onde está a classe  `App`, responsável por inciar a aplicação;
- `nucleo`: onde estão as classes principais, responsáveis pela lógica central,
processamento das mensagens, gerenciamento do inventário de serviços e o
mapeamento com as preferências de protocolo de cada endereço;
- `protocolos`: onde estão configurados os protocolos e seus controladores; e
- `utils`: onde as classes utilitárias estão definidas.

Ao inciar o S3B, o broker MQTT HiveMQ é iniciado com as configurações definidas no
arquivo `config.xml` localizado no diretório de recursos do módulo `protocolos`. Por 
padrão, ele foi definido para aceitar apenas conexões TCP em rede local. Para mais
detalhes da configuração, a documentação do HiveMQ pode ser consultada 
[aqui](https://docs.hivemq.com/hivemq/latest/user-guide/index.html).

Para alterar as configurações do protocolos, deve acessar as classes dos respectivos
protocolos no módulo `protocolos`. 

A configuração do protocolo CoAP pode gerar erros de configurado com `localhost`. Se
ocorrer, execute o comando `ipconfig` (Windows) ou equivalente em um terminal e use
o endereço IPv4 associado a sua máquina pela sua rede Wi-Fi ou Ethernet.

Caso queira adicionar novos, precisará criar a classe do protocolo 
herdando da classe abstrata `Protocolo` e criar um controlador para a classe usando 
a classe abstrata `ControladorDoBarramento`.
