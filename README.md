# RetryWithRestTemplate
Projeto que mostra um exemplo prático de como criar um mecanismo de retentativas de chamada a uma API de terceiros, utilizando Spring Retry e RestTemplate

Nesse projeto, é utilizada a API "Viacep" como exemplo, mas pode ser utilizada qualquer aplicação terceira.
O objetivo é que, ao fazer uma requisição para essa API utilizando RestTemplate, caso ocorra qualquer problema no lado dela (algum status code 5xx),
nós possamos executar novas tentativas programáticas após de n em n segundos, por x vezes e, em cada tentativa, termos listeners que possam executar alguma ação, seja ao iniciar uma nova tentativa, ao finalizar ou até mesmo quando obter um erro como resposta.
