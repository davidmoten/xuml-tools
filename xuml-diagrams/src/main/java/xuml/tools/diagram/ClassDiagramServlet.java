package xuml.tools.diagram;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.Marshaller;

import com.google.common.base.Optional;

public class ClassDiagramServlet extends HttpServlet {

	private static final long serialVersionUID = 2511746331522695068L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String xml = Context.instance().getDatastore()
				.get("diagram", id + "-model", "model");
		Optional<String> viewJson = Optional.fromNullable(req
				.getParameter("view"));
		createClassDiagram(req, resp, xml, viewJson);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String xml = req.getParameter("xml");
		Optional<String> viewJson = Optional.fromNullable(req
				.getParameter("view"));
		System.out.println(xml);
		System.out.println(viewJson);
		createClassDiagram(req, resp, xml, viewJson);
	}

	private void createClassDiagram(HttpServletRequest req,
			HttpServletResponse resp, String xml, Optional<String> viewJson)
			throws IOException {
		Domains domains = new Marshaller()
				.unmarshal(IOUtils.toInputStream(xml));
		String domainString = req.getParameter("domain");
		if (domainString == null)
			domainString = "1";
		int domain = Integer.parseInt(domainString) - 1;
		String ssString = req.getParameter("ss");
		if (ssString == null)
			ssString = "1";
		int ss = Integer.parseInt(ssString) - 1;
		String html = new ClassDiagramGenerator().generate(domains, domain, ss,
				viewJson);
		resp.setContentType("text/html");
		IOUtils.copy(IOUtils.toInputStream(html), resp.getOutputStream());
	}

}
