package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.Dados;
import br.com.alura.tabelafipe.model.Modelos;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConsumoApi;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){

        var menu = """
                *** opções ***
                
                carro
                moto
                caminhão
                
                digite uma das opções para consultar
                """;

        System.out.println(menu);

        var opcao = leitura.nextLine();

        String endereco;

        if(opcao.contains("car")){
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco =  URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("informe o codigo da marca para consulta");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca  + "/modelos";
        json = consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nmodelos desta marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\ndigite um trecho do nome do carro a ser buscado: ");

        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nmodelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\ndigite o código do modelo para buscar os valores de avaliacao");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos =  conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i =0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n todos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);

    }



}
