package estruturaGrafo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrafoLista implements Cloneable{

    ArrayList<Vertice> listaVertice = new ArrayList<>();
    ArrayList<String> vesticesVisitados = new ArrayList<>();
    /*BSF*/
    private Stack<Vertice> filaVertices;

    /*DSF*/
    private Stack<Vertice> pilhaVertices;

    private final boolean direcional;
    private final boolean arestasTemPeso;

    public GrafoLista(boolean direcional, boolean arestasTemPeso) {
        this.pilhaVertices = new Stack<>();
        this.filaVertices = new Stack<>();
        this.direcional = direcional;
        this.arestasTemPeso = arestasTemPeso;
    }

    public boolean insereVertice(Vertice novoVertice) {
        for (Vertice vertice : listaVertice) {
            if (vertice.getRotulo().equals(novoVertice.getRotulo())) {
                return false;
            }
        }

        this.listaVertice.add(novoVertice);
        return true;
    }

    public boolean inserirAresta(String rotulo_origem, String rotulo_destino, int pesoAresta) {
        boolean possivel = false;
        if ((verificaVertice(rotulo_origem)) && (verificaVertice(rotulo_destino)) && (!rotulo_origem.equals(rotulo_destino))) {
            for (Vertice vertice : listaVertice) {
                if (vertice.getRotulo().equals(rotulo_origem)) {
                    if (arestasTemPeso) {
                        possivel = vertice.insereAresta(new Aresta(vertice.getRotulo(),rotulo_destino, pesoAresta));
                    } else {
                        possivel = vertice.insereAresta(new Aresta(rotulo_destino));
                    }
                }
                if (!direcional) {
                    if (vertice.getRotulo().equals(rotulo_destino)) {
                        if (arestasTemPeso) {
                            possivel = vertice.insereAresta(new Aresta(vertice.getRotulo(), rotulo_origem, pesoAresta));
                        } else {
                            possivel = vertice.insereAresta(new Aresta(rotulo_origem));
                        }
                    }
                }
            }
        }

        return possivel;
    }

    public boolean verificaVertice(String vertice_verificar) {
        boolean existe = false;
        for (Vertice vertice : listaVertice) {
            if (vertice.getRotulo().equals(vertice_verificar)) {
                existe = true;
            }
        }
        return existe;
    }

    public boolean verificaAresta(String origem, String destino) {
        boolean exite = false;
        for (Vertice vertice : listaVertice) {
            if (vertice.getRotulo().equals(origem)) {
                for (int i = 0; i < vertice.getListaAresta().size(); i++) {
                    if (vertice.getListaAresta().get(i).getDestino().equals(destino)) {
                        exite = true;
                    }
                }
            }
        }
        return exite;
    }

    public String removeVertice(String vertice_remove) {
        if (verificaVertice(vertice_remove)) {
            for (int i = 0; i < this.listaVertice.size(); i++) {
                if (this.listaVertice.get(i).getRotulo().equals(vertice_remove)) {
                    this.listaVertice.remove(i);
                    for (Vertice vertice : listaVertice) {
                        for (int j = 0; j < vertice.getListaAresta().size(); j++) {
                            if (vertice.getListaAresta().get(j).getVertice().equals(vertice_remove)) {
                                vertice.getListaAresta().remove(j);
                            }
                        }
                    }
                }
            }
            return "Vertice " + vertice_remove + " removido com sucesso";
        }
        return "Não foi encontrado nenhum vertice " + vertice_remove;
    }

    public String removeAresta(String origem, String destino) {
        if (verificaAresta(origem, destino)) {
            for (Vertice vertice : listaVertice) {
                if (vertice.getRotulo().equals(origem)) {
                    for (int i = 0; i < vertice.getListaAresta().size(); i++) {
                        if (vertice.getListaAresta().get(i).getDestino().equals(destino)) {
                            vertice.getListaAresta().remove(i);
                        }
                    }
                }
            }
            return "Aresta " + origem + "--->" + destino + " removida com sucesso!";
        }
        return "Aresta " + origem + "--->" + destino + " não encontrada";
    }

    public String imprimeGrafo() {
        String grafo = "";

        for (Vertice vertice : listaVertice) {
            grafo = grafo + vertice.getRotulo() + ": ";

            for (Aresta aresta : vertice.getListaAresta()) {
                if (arestasTemPeso) {
                    grafo = grafo + aresta.getDestino() + "(" + aresta.getPeso() + ") ";
                } else {
                    grafo = grafo + aresta.getDestino() + " ";
                }
            }
            grafo = grafo + "\n";
        }

        return grafo;
    }

    public int getQuantidadeArestas() {
        int qtdArestas = 0;

        for (Vertice vertice : listaVertice) {
            qtdArestas = qtdArestas + vertice.getListaAresta().size();
        }

        if (!direcional) {
            qtdArestas = qtdArestas / 2;
        }

        return qtdArestas;
    }

    public boolean possuiTresCiclos() {
        boolean possivel = false;
        for (int i = 0; i < listaVertice.size(); i++) {
            for (int j = 0; j < listaVertice.size(); j++) {
                if (verificaAresta(listaVertice.get(i).getRotulo(), listaVertice.get(j).getRotulo())) {
                    for (int k = 0; k < listaVertice.size(); k++) {
                        if (verificaAresta(listaVertice.get(j).getRotulo(), listaVertice.get(k).getRotulo())) {
                            if ((verificaAresta(listaVertice.get(k).getRotulo(), listaVertice.get(i).getRotulo())) && (i != j) && (i != k) && (j != k)) {
                                //System.out.println(listaVertice.get(i).getRotulo());
                                possivel = true;
                            }
                        }
                    }
                }
            }
        }
        return possivel;
    }

    public boolean verificaPlanar() {
        int qtd_vertice, qtd_arestas;

        if (listaVertice.size() <= 2) {
            return true;
        }

        qtd_vertice = listaVertice.size();
        qtd_arestas = getQuantidadeArestas();

        if (possuiTresCiclos()) {
            return (qtd_arestas <= (3 * qtd_vertice - 6));
        } else {
            return (qtd_arestas <= (2 * qtd_vertice - 4));
        }
    }

    public ArrayList<Aresta> retornarArestas(String rotulo_vertice) {
        for (Vertice vertice : listaVertice) {
            if (vertice.getRotulo().equals(rotulo_vertice)) {
                return vertice.getListaAresta();
            }
        }
        return null;
    }

    public Vertice getVertice(String rotulo) {
        for (Vertice vertice : listaVertice) {
            if (vertice.getRotulo().equals(rotulo)) {
                return vertice;
            }
        }
        return null;
    }

    public ArrayList<String> bfs(String vertice) {
        Vertice verticeAtual = getVertice(vertice);
        verticeAtual.setVisitado(true);
        this.filaVertices.add(verticeAtual);
        while (!this.filaVertices.isEmpty()) {
            this.vesticesVisitados.add(this.filaVertices.firstElement().getRotulo());
            this.filaVertices.remove(0);
            for (Aresta aresta : verticeAtual.getListaAresta()) {
                if (!getVertice(aresta.getVertice()).getVisitado()) {
                    getVertice(aresta.getVertice()).setVisitado(true);
                    this.filaVertices.add(getVertice(aresta.getVertice()));
                }
            }
        }
        for (Vertice verticeAll : listaVertice) {
            if (!verticeAll.getVisitado()) {
                bfs(verticeAll.getRotulo());
            }
        }
        return this.vesticesVisitados;
    }

    public void criarGrafoTeste() {

//==============================================================================        
//        Exemplo slide (Aula)        
//==============================================================================
//        Vertice a = new Vertice("A");
//        Vertice b = new Vertice("B");
//        Vertice c = new Vertice("C");
//        Vertice d = new Vertice("D");
//        Vertice e = new Vertice("E");
//        Vertice f = new Vertice("F");
//        insereVertice(a);
//        insereVertice(b);
//        insereVertice(c);
//        insereVertice(d);
//        insereVertice(e);
//        insereVertice(f);
//        
//        inserirAresta("A", "D", 2);
//        inserirAresta("A", "C", 7);  
//        inserirAresta("A", "E", 10);         
//        inserirAresta("D", "E", 7);  
//        inserirAresta("D", "F", 4);          
//        inserirAresta("C", "E", 9);  
//        inserirAresta("C", "F", 3);          
//        inserirAresta("F", "E", 8);  
//        inserirAresta("F", "B", 2);          
//        inserirAresta("B", "C", 3);  

//==============================================================================
//        Grafo de exemplo (PRIM) 
//        Origem: https://pt.wikipedia.org/wiki/Algoritmo_de_Prim
//==============================================================================

//        Vertice a = new Vertice("0");
//        Vertice b = new Vertice("1");
//        Vertice c = new Vertice("2");
//        Vertice d = new Vertice("3");
//        Vertice e = new Vertice("4");
//        Vertice f = new Vertice("5");
//        Vertice g = new Vertice("6");
//        Vertice h = new Vertice("7");
//        insereVertice(a);
//        insereVertice(b);
//        insereVertice(c);
//        insereVertice(d);
//        insereVertice(e);
//        insereVertice(f);
//        insereVertice(g);
//        insereVertice(h);
//        
//        inserirAresta("6", "7", 9);
//        inserirAresta("3", "5", 7);
//        inserirAresta("2", "1", 7);
//        inserirAresta("1", "4", 6);
//        inserirAresta("3", "6", 5);
//        inserirAresta("2", "6", 3);
//        inserirAresta("0", "1", 3);
//        inserirAresta("4", "5", 2);
//        inserirAresta("1", "3", 2);
//        inserirAresta("2", "7", 2);
//        inserirAresta("5", "6", 1);
//        inserirAresta("0", "2", 1);    

//==============================================================================
//      Grafo de exemplo (KRUSKAL e Prim) 
//      Origem: https://stackoverflow.com/questions/1195872/kruskal-vs-prim
//==============================================================================

        Vertice a = new Vertice("A");
        Vertice b = new Vertice("B");
        Vertice c = new Vertice("C");
        Vertice d = new Vertice("D");
        Vertice e = new Vertice("E");
        Vertice f = new Vertice("F");        
        insereVertice(a);
        insereVertice(b);
        insereVertice(c);
        insereVertice(d);
        insereVertice(e);
        insereVertice(f);                
        inserirAresta("A", "B", 4);
        inserirAresta("A", "C", 4);
        inserirAresta("C", "B", 2);
        inserirAresta("B", "D", 3);
        inserirAresta("B", "E", 4);
        inserirAresta("B", "F", 2);
        inserirAresta("D", "E", 3);
        inserirAresta("E", "F", 3);
        
    }

    public void resetVisitados() {
        for (Vertice vertice : listaVertice) {
            vertice.setVisitado(false);
            vertice.setRotulo_pai("");
        }
        this.vesticesVisitados.clear();
    }

    public ArrayList<String> dfs(String rotulo_origem) {
        Vertice verticeAtual = getVertice(rotulo_origem);
        verticeAtual.setVisitado(true);
        this.vesticesVisitados.add(verticeAtual.getRotulo());
        this.pilhaVertices.add(verticeAtual);
        for (Aresta aresta : verticeAtual.getListaAresta()) {
            if (!getVertice(aresta.getVertice()).getVisitado()) {
                dfs(aresta.getVertice());
            }
        }
        this.pilhaVertices.remove(verticeAtual);
        if (!this.pilhaVertices.isEmpty()) {
            for (Vertice vertice : listaVertice) {
                if (!vertice.getVisitado()) {
                    dfs(vertice.getRotulo());
                }
            }
        } else {
            return this.vesticesVisitados;
        }
        return null;
    }

    public String dijkstra(String rotulo_origem, String rotulo_destino) {
        //O rótulo destino pode ser vazio, verificar as diferenças do que 
        //deve ser retornado.
        List<Vertice> menorCaminho = new ArrayList<>();
        List<Vertice> naoVisitados = new ArrayList<>();
        Vertice vertice_atual;
        Vertice vertice_vizinho;
        Vertice vertice_caminho;
        String resposta = "";

        resetVisitados();

        if ((!verificaVertice(rotulo_origem)) || ((!verificaVertice(rotulo_destino)) && (!rotulo_destino.equals("")))) {
            return "Pelo menos um dos vértices informados não existem.";
        }

        menorCaminho.add(getVertice(rotulo_origem));

        for (int i = 0; i < listaVertice.size(); i++) {
            if (listaVertice.get(i).getRotulo().equals(rotulo_origem)) {
                listaVertice.get(i).setDistancia(0);
            } else {
                listaVertice.get(i).setDistancia(9999);
            }
            naoVisitados.add(listaVertice.get(i));
        }

        Collections.sort(naoVisitados);

        while (!naoVisitados.isEmpty()) {                        
            vertice_atual = naoVisitados.get(0);
            resposta = resposta + "\nPegou o vértice " + vertice_atual.getRotulo() + ", distância: " + vertice_atual.getDistancia() + ", pai: " + vertice_atual.getRotulo_pai();

            if (!rotulo_destino.equals("")) {
                if (getVertice(rotulo_destino).equals(vertice_atual)) {
                    resposta = resposta + "\nChegou ao destino!";
                    break;
                }
            }

            for (int i = 0; i < vertice_atual.getListaAresta().size(); i++) {
                vertice_vizinho = getVertice(vertice_atual.getListaAresta().get(i).getDestino());
                resposta = resposta + "\nVerificando o vizinho de " + vertice_atual.getRotulo() + ": " + vertice_vizinho.getRotulo();

                if (!vertice_vizinho.getVisitado()) {
                    if (vertice_vizinho.getDistancia() > (vertice_atual.getDistancia() + vertice_atual.getListaAresta().get(i).getPeso())) {
                        vertice_vizinho.setDistancia(vertice_atual.getDistancia() + vertice_atual.getListaAresta().get(i).getPeso());
                        vertice_vizinho.setRotulo_pai(vertice_atual.getRotulo());

                        if (vertice_vizinho.getRotulo().equals(rotulo_destino)) {
                            menorCaminho.clear();
                            vertice_caminho = vertice_vizinho;

                            menorCaminho.add(vertice_vizinho);

                            while (!vertice_caminho.getRotulo_pai().equals("")) {
                                menorCaminho.add(getVertice(vertice_caminho.getRotulo_pai()));
                                vertice_caminho = getVertice(vertice_caminho.getRotulo_pai());
                            }

                            Collections.sort(menorCaminho);

//                            resposta = "";
//                                    
//                            for (Vertice caminho : menorCaminho) {
//                                resposta = resposta + " | " + caminho.getRotulo() + "(" +caminho.distancia + ") |";
//                            }
//                            
//                            return resposta;
                        }
                    }
                }
            }

            vertice_atual.setVisitado(true);
            naoVisitados.remove(vertice_atual);

            Collections.sort(naoVisitados);

            resposta = resposta + "\nNão foram visitados ainda:";
            for (Vertice naoVisitado : naoVisitados) {
                resposta = resposta + " | " + naoVisitado.getRotulo() + " Distância (" + naoVisitado.getDistancia() + ")" + " | ";
            }
        }

        return resposta;
    }

    public ArrayList<Vertice> getListaVertice() {
        return listaVertice;
    }  
    
    public ArrayList<Vertice> clonarListaVertice() {
        ArrayList<Vertice> vertices = new ArrayList<>();
        
        for (Vertice vertice : listaVertice) {
            try {
                vertices.add(vertice.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(GrafoLista.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return vertices;
    }  
    
    public void resetCores(){
        for (Vertice vertice : listaVertice) {
            vertice.setIndiceCor(0);
            vertice.setCor(null);
        }
    }
    
    public ArrayList<Vertice> getVizinhos(String vertice){
        ArrayList<Vertice> listaVizinhos = new ArrayList<>();        
        for (Aresta aresta : getVertice(vertice).getListaAresta()) {
            listaVizinhos.add(getVertice(aresta.getDestino()));
        }        
        return listaVizinhos;
    }
    
    public ArrayList<Aresta> getArestasGrafo(){
        ArrayList<Aresta> arestas = new ArrayList<>();
        
        //Verificar a questão de Grafo direcional ou não. Como se comportará?
        for (Vertice vertice : listaVertice) {
            for (Aresta aresta : vertice.getListaAresta()){
                Aresta nova_aresta = new Aresta("");
                
                try {
                    nova_aresta = aresta.clone();
                    arestas.add(nova_aresta);
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(GrafoLista.class.getName()).log(Level.SEVERE, null, ex);
                }                                
            }
        }
        
        return arestas;
    } 
    
    public boolean getGrafoDirecional(){
        return direcional;
    }
    
    public boolean getArestasTemPeso(){
        return arestasTemPeso;
    }
    
    public void insereListaVertices(ArrayList<Vertice> vertices){
        for (Vertice vertice : vertices) {
            insereVertice(vertice);
        }
    }    
    
    public void removeArestas(){
        for (Vertice vertice : listaVertice) {
            vertice.removeArestas();
        }
    }
    
    @Override
    public GrafoLista clone() throws CloneNotSupportedException {
        GrafoLista novo_grafo = (GrafoLista) super.clone();        
        novo_grafo.listaVertice = this.clonarListaVertice();        
        return novo_grafo;
    }
}