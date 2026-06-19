package view;

import control.Controle;
import model.Evento;
import model.Usuario;
import model.UsuarioEventos;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Tela {

    // Cria e administra o Menu
    public void criarTela() {
        Scanner scanner = new Scanner(System.in);
        menu();
        String numero = "0";
        while (!numero.equals("6")) {
            System.out.println(" ");
            System.out.print("Digite uma opção: ");
            numero = scanner.nextLine();
            switch (numero) {
                case "1":
                    menu();
                    break;
                case "2":
                    menu();
                    cadastraUsuario(scanner);
                    break;
                case "3":
                    menu();
                    cadastraEvento(scanner);
                    break;
                case "4":
                    menu();
                    listarEventos(scanner);
                    break;
                case "5":
                    menu();
                    confirmar(scanner);
                    break;
                case "6":
                    break;
                default:
                    menu();
                    System.out.println("Opção " + numero + " e invalida!");
                    break;
            }
        }
        scanner.close();
    }

    private static void menu() {
        System.out.println("_____________________________________________________________________________________________");
        System.out.println("| Menu -> 1 | Usuario -> 2 | Evento -> 3 | Listar Eventos -> 4 | Confirmar -> 5 | Sair -> 6 |");
        System.out.println("_____________________________________________________________________________________________");
    }

    private static void cadastraUsuario(Scanner scanner) {
        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite e-mail: ");
        String email = scanner.nextLine();
        System.out.print("Digite CPF: ");
        String cpf = scanner.nextLine();
        Controle.cadastraUsuario(nome, email, cpf);
    }

    private static void cadastraEvento(Scanner scanner) {
        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite endereco: ");
        String endereco = scanner.nextLine();
        System.out.println("Categorias Festa = 1, Evento Esportivo = 2, Evento Religioso = 3, Show = 4, Encontro = 5 ");
        System.out.print("Digite categoria: ");
        String categoria = scanner.nextLine();
        System.out.print("Digite horario [DD/MM/AAAA HH:MM]: ");
        String horario = scanner.nextLine();
        System.out.print("Digite descricao: ");
        String descricao = scanner.nextLine();
        Controle.cadastraEvento(nome, endereco, categoria, horario, descricao);
    }

    private static void listarEventos(Scanner scanner) {
        System.out.print("Digite CPF do usuario: ");
        String cpf = scanner.nextLine();
        Usuario usuario = Controle.geUsuario(cpf);
        ArrayList<Evento> eventos =  Controle.geEventos();
        if (usuario != null) {
            ArrayList<UsuarioEventos> usuarioEventos = Controle.getUsuarioEventos(usuario.getCpf());
            System.out.println("_____________________________________");
            printListLocal(eventos, usuarioEventos);
        } else {
            System.err.print("Usuario não Encontrado para o CPF: " + cpf);
            System.out.println("_____________________________________");
            System.out.println("| Eventos                            |");
            System.out.println("_____________________________________");
            System.out.println("| ID | Nome | Endereco | Categoria | Horario | Descricao | Status |");
            System.out.println("_____________________________________");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Evento ev: eventos) {
                String formattedDateTime = ev.getHorario().format(formatter);
                System.out.println("| " + ev.getId() + " | " + ev.getNome() + " | " + ev.getEndereco() + " | " + ev.getCategoria().getDescription() + " | " + formattedDateTime + " | " + ev.getDescricao()  + " | " +  ev.getStatusHorario() + " |");
            }
        }
    }

    private static void printListLocal(ArrayList<Evento> eventos, ArrayList<UsuarioEventos> usuarioEventos) {
        System.out.println("| Eventos                            |");
        System.out.println("________________________________________________________________________________");
        System.out.println("| ID | Nome | Endereco | Categoria | Horario | Descricao | Status | Registrado |");
        System.out.println("________________________________________________________________________________");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Evento ev: eventos) {
            String resposta = "Nao";
            for (UsuarioEventos eu: usuarioEventos) {
                if (Objects.equals(ev.getId(), eu.getEvento().getId())) {
                    resposta = "Sim";
                    break;
                }
            }
            String formattedDateTime = ev.getHorario().format(formatter);
            System.out.println("| " + ev.getId() + " | " + ev.getNome() + " | " + ev.getEndereco() + " | " + ev.getCategoria().getDescription() + " | " + ev.getHorario() + " | " +  formattedDateTime + " | "  + ev.getDescricao() + " | "  + resposta + " |");
        }
    }

    // Funcao responsavel por confirmar ou desconfirmar a presenca de um usuario
    private static void confirmar(Scanner scanner) {
        System.out.print("Digite CPF do usuario: ");
        String cpf = scanner.nextLine();
        Usuario usuario = Controle.geUsuario(cpf);
        if (usuario != null) {
            ArrayList<UsuarioEventos> usuarioEventos = Controle.getUsuarioEventos(usuario.getCpf());
            System.out.println("__________________________________");
            System.out.println("| Eventos Confirmados do Usuario |");
            System.out.println("__________________________________");
            System.out.println("| ID | Nome Evento |");
            System.out.println("____________________");
            for (UsuarioEventos ue: usuarioEventos) {
                System.out.println("| " + ue.getEvento().getId() + " | " + ue.getEvento().getNome() +" |");
            }
            System.out.print("Digite 1 para Incluir ou 2 para Excluir uma Confirmacao: ");
            String tipo = scanner.nextLine();
            if (tipo.equals("1") || tipo.equals("2")) {
                if (tipo.equals("1")) {
                    ArrayList<Evento> eventos =  Controle.geEventos();
                    System.out.println("_____________________________________");
                    printListLocal(eventos, usuarioEventos);
                    System.out.print("Digite o ID do Evento que quer incluir para o Usuario: ");
                    String incluir = scanner.nextLine();
                    Controle.incluirConfirmacao(incluir, cpf, usuarioEventos);
                } else {
                    System.out.print("Digite o ID do Evento que quer excluir para o Usuario: ");
                    String excluir = scanner.nextLine();
                    Controle.excluirConfirmacao(excluir, usuarioEventos);
                }
            } else {
                System.err.print("Tipo de acao incorreta: " + tipo);
            }
        } else {
            System.err.print("Usuario não Encontrado para o CPF: " + cpf);
        }
    }

}
