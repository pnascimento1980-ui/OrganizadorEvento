package control;

import model.EnumCategoria;
import model.Evento;
import model.Usuario;
import model.UsuarioEventos;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Controle {

    public static void cadastraUsuario(String nome, String email, String cpf) {
        getArquivo("users.data");
        Usuario usuario = new Usuario(nome, email, cpf);
        ArrayList<Usuario> usuarios = geUsuarios(getLinhas("users.data"));
        salvaUsuario(usuarios, usuario);
    }

    public static void cadastraEvento(String nome, String endereco, String categoria, String horario, String descricao) {
        if (!categoria.equals("1") && !categoria.equals("2") && !categoria.equals("3") && !categoria.equals("4") && !categoria.equals("5")) {
            System.err.print("Categoria incorreta!: " + categoria);
        } else {
            if (validarDataHora(horario)) {
                getArquivo("events.data");
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
                Evento evento = new Evento(0L, nome, endereco, EnumCategoria.fromCode(Integer.parseInt(categoria)), LocalDateTime.parse(horario, formatador), descricao);
                ArrayList<Evento> eventos = geEventos(getLinhas("events.data"));
                salvaEvento(eventos, evento);
            } else {
                System.err.print("Horario incorreto!: " + horario);
            }
        }
    }

    public static Usuario geUsuario(String cpf) {
        getArquivo("users.data");
        Usuario usuario = null;
        boolean apenasNumeros = cpf.matches("[0-9]+");
        if (!apenasNumeros) {
            System.err.print("So pode ter numeros no cpf: " + cpf);
        } else if (cpf.length() != 11) {
            System.err.print("O cpf deve ter 11 digitos: " + cpf);
        } else {
            ArrayList<Usuario> usuarios = geUsuarios(getLinhas("users.data"));
            for (Usuario usu: usuarios) {
                if (usu.getCpf().equals(cpf)) {
                    usuario = usu;
                }
            }
        }
        return usuario;
    }

    public static ArrayList<Evento> geEventos() {
        getArquivo("events.data");
        return geEventos(getLinhas("events.data"));
    }

    public static ArrayList<UsuarioEventos> getUsuarioEventos(String cpf) {
        getArquivo("events.data");
        getArquivo("user_events.data");
        ArrayList<UsuarioEventos> usuarioEventos = new ArrayList<>();
        ArrayList<String> linhas = getLinhas("user_events.data");
        ArrayList<Evento> eventos = geEventos(getLinhas("events.data"));
        if (!eventos.isEmpty()) {
            for (String linha : linhas) {
                String[] partes = linha.split(";");
                Usuario usuario = new Usuario(null, null, partes[0]);
                if (usuario.getCpf().equals(cpf)) {
                    Evento evento = null;
                    for (Evento evo: eventos) {
                        if (evo.getId() == Long.parseLong(partes[1])) {
                            evento = evo;
                        }
                    }
                    usuarioEventos.add(new UsuarioEventos(usuario, evento));
                }
            }
        }
        return usuarioEventos;
    }

    public static void excluirConfirmacao(String id, ArrayList<UsuarioEventos> usuarioEventos) {
        boolean encontrou = false;
        ArrayList<UsuarioEventos> novas = new ArrayList<>();
        for (UsuarioEventos ue: usuarioEventos) {
            if (ue.getEvento().getId() == Long.parseLong(id)) {
                encontrou = true;
            } else {
                novas.add(ue);
            }
        }
        if (encontrou) {
            try (FileWriter writer = new FileWriter("user_events.data")) {
                writer.write("");
            } catch (IOException e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }
            for (UsuarioEventos nova: novas) {
                salvarLinha("user_events.data", nova.getUsuario().getCpf() + ";" + nova.getEvento().getId());
            }
            System.out.println("O Evento Desconfirmado para o ID: " + id);
        } else {
            System.err.print("Não foi encontrado o Evento com ID: " + id);
        }
    }

    public static void incluirConfirmacao(String id, String cpf, ArrayList<UsuarioEventos> usuarioEventos) {
        boolean encontrou = false;
        for (UsuarioEventos ue: usuarioEventos) {
            if (ue.getEvento().getId() == Long.parseLong(id)) {
                encontrou = true;
                break;
            }
        }
        if (!encontrou) {
            salvarLinha("user_events.data", cpf + ";" + id);
            System.out.println("O Evento Confirmado para o ID: " + id);
        } else {
            System.err.print("O Evento com ID: " + id + " não existe!");
        }
    }

    private static void getArquivo(String nome) {
        File arquivo = new File(nome);
        try {
            arquivo.createNewFile();
        } catch (IOException e) {
            System.err.print("Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    private static void salvarLinha(String patch, String linha) {
        try (FileWriter fw = new FileWriter(patch, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
             out.println(linha);
        } catch (IOException e) {
            System.err.print("Erro ao gravar o arquivo: " + e.getMessage());
        }
    }

    private static ArrayList<String> getLinhas(String patch) {
        ArrayList<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(patch))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.err.print("Erro ao ler o arquivo: " + e.getMessage());
        }
        return linhas;
    }

    private static ArrayList<Usuario> geUsuarios(ArrayList<String> linhas) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        for (String linha : linhas) {
            String[] partes = linha.split(";");
            usuarios.add(new Usuario(partes[0], partes[1], partes[2]));
        }
        return usuarios;
    }

    private static ArrayList<Evento> geEventos(ArrayList<String> linhas) {
        ArrayList<Evento> eventos = new ArrayList<>();
        for (String linha : linhas) {
            String[] partes = linha.split(";");
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
            eventos.add(new Evento(Long.parseLong(partes[0]), partes[1], partes[2], EnumCategoria.fromCode(Integer.parseInt(partes[3])), LocalDateTime.parse(partes[4], formatador), partes[5]));
        }
        return eventos;
    }

    private static void salvaUsuario(ArrayList<Usuario> usuarios, Usuario usuario) {
        boolean apenasNumeros = usuario.getCpf().matches("[0-9]+");
        if (!apenasNumeros) {
            System.err.print("So pode ter numeros no cpf: " + usuario.getCpf());
        } else if (usuario.getCpf().length() != 11) {
            System.err.print("O cpf deve ter 11 digitos: " + usuario.getCpf());
        } else {
            boolean existe = false;
            for (Usuario usu: usuarios) {
                if (usu.getCpf().equals(usuario.getCpf())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                salvarLinha("users.data", usuario.getNome() + ";" + usuario.getEmail() + ";" + usuario.getCpf());
                System.out.print("Usuario salvo com sucesso!");
            } else {
                System.err.print("Já existe um usuario com o cpf: " + usuario.getCpf());
            }
        }
    }

    private static void salvaEvento(ArrayList<Evento> eventos, Evento evento) {
        boolean existe = false;
        Long id = 0L;
        for (Evento eve: eventos) {
            if (eve.getNome().equals(evento.getNome())) {
                existe = true;
            }
            id = eve.getId();
        }
        ++id;
        if (!existe) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = evento.getHorario().format(formatter);
            salvarLinha("events.data", id.toString() + ";" + evento.getNome() + ";" + evento.getEndereco() + ";" + evento.getCategoria().getCode() + ";" + formattedDateTime + ";" + evento.getDescricao());
            System.out.println("Evento salvo com sucesso!");
        } else {
            System.err.print("Já existe um evento com o nome: " + evento.getNome());
        }
    }

    private static boolean validarDataHora(String texto) {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            LocalDateTime.parse(texto, formatador);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
