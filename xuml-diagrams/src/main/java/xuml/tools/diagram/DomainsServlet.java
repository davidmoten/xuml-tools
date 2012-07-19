package xuml.tools.diagram;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import miuml.jaxb.Marshaller;

import org.apache.commons.io.IOUtils;

import xuml.tools.miuml.metamodel.jaxb.Domain;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.util.xml.TaggedString;

public class DomainsServlet extends HttpServlet {

	private static final long serialVersionUID = 2511746331522695068L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String xml = Context.instance().getDatastore()
				.get("diagram", id + "-model", "model");
		Domains domains = new Marshaller()
				.unmarshal(IOUtils.toInputStream(xml));

		TaggedString t = new TaggedString();
		for (JAXBElement<? extends Domain> domain : domains.getDomain()) {
			t.startTag("h2");
			t.append(domain.getValue().getName());
			t.closeTag();
			if (domain.getValue() instanceof ModeledDomain) {
				ModeledDomain d = (ModeledDomain) domain.getValue();
				int ssNumber = 0;
				for (Subsystem ss : d.getSubsystem()) {
					ssNumber++;
					t.startTag("a");
					t.addAttribute("href", "cd?id=" + id + "&ss=" + ssNumber);
					t.append(ss.getName());
					t.closeTag();
					t.startTag("br");
					t.closeTag();
				}
			}
		}
		t.close();

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print(t);
	}
}