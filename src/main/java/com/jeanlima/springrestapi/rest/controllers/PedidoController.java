
package com.jeanlima.springrestapi.rest.controllers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.jeanlima.springrestapi.model.Cliente;
import com.jeanlima.springrestapi.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapi.enums.StatusPedido;
import com.jeanlima.springrestapi.model.ItemPedido;
import com.jeanlima.springrestapi.model.Pedido;
import com.jeanlima.springrestapi.rest.dto.AtualizacaoStatusPedidoDTO;
import com.jeanlima.springrestapi.rest.dto.InformacaoItemPedidoDTO;
import com.jeanlima.springrestapi.rest.dto.InformacoesPedidoDTO;
import com.jeanlima.springrestapi.rest.dto.PedidoDTO;
import com.jeanlima.springrestapi.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteController clienteController;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save( @RequestBody PedidoDTO dto ){
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    public InformacoesPedidoDTO getById( @PathVariable Integer id ){
        return service
                .obterPedidoCompleto(id)
                .map( p -> converter(p) )
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<InformacoesPedidoDTO> getByClienteId (@PathVariable Integer clienteId) {
        Cliente c = clienteController.getClienteById(clienteId);
        List<InformacoesPedidoDTO> pedidos = new ArrayList<>();
        for (Pedido p: pedidoRepository.findByCliente(c)) {
            pedidos.add(converter(p));
        }
        return pedidos;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        pedidoRepository.findById(id)
                .map( pedido -> {
                    pedidoRepository.delete(pedido );
                    return pedido;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado") );
    }

    private InformacoesPedidoDTO converter(Pedido pedido){
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .items(converter(pedido.getItens()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens){
        if(CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InformacaoItemPedidoDTO
                            .builder()
                            .descricaoProduto(item.getProduto().getDescricao())
                            .precoUnitario(item.getProduto().getPreco())
                            .quantidade(item.getQuantidade())
                            .build()
        ).collect(Collectors.toList());
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable Integer id ,
                             @RequestBody AtualizacaoStatusPedidoDTO dto){
        String novoStatus = dto.getNovoStatus();
        service.atualizaStatus(id, StatusPedido.valueOf(novoStatus));
    }

    @GetMapping("/verifica/{id}")
    public void verificaEstoque(@PathVariable Integer id) {
        try{
            service.verificaEstoqueProcessaPedido(id);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
