package com.jeanlima.springrestapi.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.jeanlima.springrestapi.model.*;
import com.jeanlima.springrestapi.repository.*;
import org.springframework.stereotype.Service;

import com.jeanlima.springrestapi.enums.StatusPedido;
import com.jeanlima.springrestapi.exception.PedidoNaoEncontradoException;
import com.jeanlima.springrestapi.exception.RegraNegocioException;
import com.jeanlima.springrestapi.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapi.rest.dto.PedidoDTO;
import com.jeanlima.springrestapi.service.PedidoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository repository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;
    private final EstoqueRepository estoqueRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        double total = 0.0;
        for (ItemPedidoDTO i: dto.getItems()){
            total += i.getQuantidade().doubleValue() * i.getProduto();
        }
        pedido.setTotal(BigDecimal.valueOf(total));
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        return pedido;
    }
    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }
    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        
        return repository.findByIdFetchItens(id);
    }
    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        repository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return repository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException() );
        
    }

    @Override
    public void verificaEstoqueProcessaPedido(Integer id) throws Exception {
        Optional<Pedido> pedido = repository.findById(id);
        if(pedido.isPresent()){
            List<ItemPedido> itensPedido = pedido.get().getItens();
            for (ItemPedido i: itensPedido) {
                Optional<Estoque> e =  estoqueRepository.findByProduto_Descricao(i.getProduto().getDescricao());
                if(e.isEmpty())
                    throw new PedidoNaoEncontradoException();
                if(e.get().getQuantidade() - i.getQuantidade() < 0)
                    throw new Exception("Não disponível no estoque");

                e.get().setQuantidade(e.get().getQuantidade() - i.getQuantidade());
                estoqueRepository.save(e.get());
            }
        }

    }
}
