package controller;

import java.io.IOException;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DAO;
import model.JavaBeans;

@WebServlet(urlPatterns = { "/main", "/insert", "/select", "/update", "/delete", "/report" })
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DAO dao = new DAO();
	JavaBeans contato = new JavaBeans();;

	public Controller() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getServletPath();
		System.out.println(action);
		if (action.equals("/main")) {
			contato(request, response);
		} else if (action.equals("/insert")) {
			novoContato(request, response);
		} else if (action.equals("/select")) {
			listarContato(request, response);
		} else if (action.equals("/update")) {
			editarContato(request, response);
		} else if (action.equals("/delete")) {
			removerContato(request, response);
		} else if (action.equals("/report")) {
			gerarRelatorio(request, response);
		} else {
			response.sendRedirect("index.html");
		}
	}

	// listar contatos
	protected void contato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Criando um objeto que ira receber os dados javabeans
		ArrayList<JavaBeans> lista = dao.listarContatos();
		// encaminhar lista par doc agenda.jsp
		request.setAttribute("contatos", lista);
		RequestDispatcher rd = request.getRequestDispatcher("agenda.jsp");
		rd.forward(request, response);
	}

	// Novos contatos
	protected void novoContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// teste do formulario
		System.out.println(request.getParameter("nome"));
		System.out.println(request.getParameter("fone"));
		System.out.println(request.getParameter("email"));
		// setar variaveis do javaBeans
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		// invocar metodo inderirContato passando objeto contato
		dao.inserirContato(contato);
		// redireciona para o documento agenda.jsp
		response.sendRedirect("main");
	}

	// editar contato
	protected void listarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// recebendo id do contato de agenda
		String idcon = request.getParameter("idcon");
		// setar variável javabeans
		contato.setIdcon(idcon);
		// executar método selecinar contato (DAO)
		dao.selecionarContato(contato);
		// teste de recebimento
		/**
		 * System.out.println(contato.getIdcon());
		 * System.out.println(contato.getNome()); System.out.println(contato.getFone());
		 * System.out.println(contato.getEmail()); }
		 **/
		// setar os atributos com o contúedo do JavaBeans
		request.setAttribute("idcon", contato.getIdcon());
		request.setAttribute("nome", contato.getNome());
		request.setAttribute("fone", contato.getFone());
		request.setAttribute("email", contato.getEmail());
		// encaminhar a editar.jsp
		RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
		rd.forward(request, response);
	}

	protected void editarContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// setar variávei javabeans
		contato.setIdcon(request.getParameter("idcon"));
		contato.setNome(request.getParameter("nome"));
		contato.setFone(request.getParameter("fone"));
		contato.setEmail(request.getParameter("email"));
		// executar metodo altera contato
		dao.alterarContato(contato);
		// redirecionar para o doc agenda.jsp
		response.sendRedirect("main");
	}

	// remover um contato
	protected void removerContato(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// recebimento do contato a excluir
		String idcon = request.getParameter("idcon");
		// setar a variável idcon JavaBeans
		contato.setIdcon(idcon);
		// executar metodo deletar contato(dao) passando objeto contato
		dao.deletarContato(contato);
		// redirecionar para o doc agenda.jsp
		response.sendRedirect("main");
	}
	
	//gerar relatorio em pdf
	protected void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Document documento = new Document();
		try {
			//tipo de conteudo
			response.setContentType("apllication/pdf");
			//nome do documento
			response.addHeader("Content-Disposition", "inline;filename=" + "contatos.pdf");
			//criar documento
			PdfWriter.getInstance(documento, response.getOutputStream());
			//abrir doc para abrir conteudo
			documento.open();
			documento.add(new Paragraph("Lista de Contatos:"));
			documento.add(new Paragraph(" "));
			//criar uma tabela
			PdfPTable tabela = new PdfPTable(3);
			//cabeçalho
			PdfPCell col1 = new PdfPCell(new Paragraph("Nome"));
			PdfPCell col2 = new PdfPCell(new Paragraph("Fone"));
			PdfPCell col3 = new PdfPCell(new Paragraph("Email"));
			tabela.addCell(col1);
			tabela.addCell(col2);
			tabela.addCell(col3);
			//popular tabela com contatos de forma dinâmica
			ArrayList<JavaBeans> lista = dao.listarContatos();
			for(int i = 0; i < lista.size(); i++) {
				tabela.addCell(lista.get(i).getNome());
				tabela.addCell(lista.get(i).getFone());
				tabela.addCell(lista.get(i).getEmail());				
			}
			documento.add(tabela);
			documento.close();
		} catch (Exception e) {
			System.out.println(e);
			documento.close();
		}		
	}
}
