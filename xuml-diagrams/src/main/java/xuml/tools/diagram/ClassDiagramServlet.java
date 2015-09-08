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
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<String> xml = Optional.fromNullable(req.getParameter("xml"));
        if (!xml.isPresent())
            xml = Optional.of("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                    + "<Domains xmlns=\"http://www.miuml.org/metamodel\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + "	xsi:schemaLocation=\"http://www.miuml.org/metamodel https://raw.github.com/davidmoten/xuml-tools/master/miuml-jaxb/src/main/resources/miuml-metamodel.xsd  http://org.github/xuml-tools/miuml-metamodel-extensions https://raw.github.com/davidmoten/xuml-tools/master/miuml-jaxb/src/main/resources/xuml-tools-miuml-metamodel-extensions.xsd\"\n"
                    + "	xmlns:xt=\"http://org.github/xuml-tools/miuml-metamodel-extensions\">\n</Domains>");
        Optional<String> viewJson = Optional.fromNullable(req.getParameter("view"));
        System.out.println(xml);
        System.out.println(viewJson);
        createClassDiagram(req, resp, xml.get(), viewJson);
    }

    private void createClassDiagram(HttpServletRequest req, HttpServletResponse resp, String xml,
            Optional<String> viewJson) throws IOException {
        Domains domains = new Marshaller().unmarshal(IOUtils.toInputStream(xml));
        String domainString = req.getParameter("domain");
        if (domainString == null)
            domainString = "1";
        int domain = Integer.parseInt(domainString) - 1;
        String ssString = req.getParameter("ss");
        if (ssString == null)
            ssString = "1";
        int ss = Integer.parseInt(ssString) - 1;
        String html = new ClassDiagramGenerator().generate(domains, domain, ss, viewJson);
        resp.setContentType("text/html");
        IOUtils.copy(IOUtils.toInputStream(html), resp.getOutputStream());
    }

}
