# Gerenciador de e-commerce - API REST 

Desenvolvida em Java através do framework Springboot.

A api possui entidades mapeadas e persistidas em banco de dados SQL através do Spring Data Jpa. As operações de CRUD são habilitadas para todas as entidades. 

# Endpoints

### GET
`/api/clientes` <br/>
`/api/clientes/{id}` <br/>
`/api/estoques` <br/>
`/api/estoques/{id}` <br/>
`/api/pedidos/{id}` <br/>
`/api/pedidos/cliente/{clienteId}` <br/>
`/api/pedidos/verifica/{id}` <br/>
`/api/produtos` <br/>
`/api/produtos/{id}` <br/>

### POST
`/api/clientes` <br/>
`/api/estoques` <br/>
`/api/pedidos` <br/>
`/api/produtos` <br/>

### DELETE
`/api/clientes/{id}` <br/>
`/api/estoques/{id}` <br/>
`/api/pedidos/{id}` <br/>
`/api/produtos/{id}` <br/>

### PUT
`/api/clientes/{id}` <br/>
`/api/estoques/{id}` <br/>
`/api/produtos/{id}` <br/>

### PATCH
`/api/clientes/{id}` <br/>
`/api/estoques/{id}` <br/>
`/api/pedidos/{id}` <br/>
`/api/produtos/{id}` <br/>